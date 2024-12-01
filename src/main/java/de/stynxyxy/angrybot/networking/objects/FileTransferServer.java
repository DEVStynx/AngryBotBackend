package de.stynxyxy.angrybot.networking.objects;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FileTransferServer {
    private boolean currentlyInUse = false;
    private boolean stopped = true;
    private String filename;
    private String path;
    private ArrayList<String> filecontent = new ArrayList<>();
    private final int PORT;
    private String consoleprefix;

    public FileTransferServer(String path,int port) {
        this.path = path;
        this.PORT = port;
        this.consoleprefix = new StringBuilder("-> FileServer Port: ").append(this.PORT).append(" ").toString();
        Thread runFileTransferThread = new Thread(this::run);
        runFileTransferThread.start();
    }
    private void run() {
        try(ServerSocket serverSocket = new ServerSocket(this.PORT)) {
            System.out.println("Started new File transfering Server on Port");

            while (true) {
                //prevents the Server from other clients joining
                if (currentlyInUse) {
                    continue;
                }
                Socket clientConnection = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
                PrintWriter writer = new PrintWriter(clientConnection.getOutputStream(),true);
                currentlyInUse = true;
                stopped = false;

                System.out.println(this.consoleprefix+"New Connection "+clientConnection.getInetAddress().toString());
                Thread handleFileTransferThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handle(clientConnection,reader,writer);
                    }
                });

                handleFileTransferThread.start();
                if (stopped) {
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handle(Socket clientConnection, BufferedReader reader, PrintWriter writer) {
        try (InputStream in = clientConnection.getInputStream()) {
            String inputLine;
            long filesize = 0;

            while ((inputLine = reader.readLine()) != null) {
                System.out.println(this.consoleprefix+inputLine);

                if (inputLine.startsWith("FILE REQUEST")) {
                    String formattedPath = this.path.replace("\\","\\\\");
                    writer.println(formattedPath);

                } else if (inputLine.startsWith("FILE INFO NAME")) {
                    this.filename = inputLine.replace("FILE INFO NAME","").trim();
                    filecontent.clear(); // Make sure, that the ArrayList is empty
                } else if(inputLine.startsWith("FILE INFO SIZE")) {
                    filesize = Long.parseLong(inputLine.replace("FILE INFO SIZE","").trim());
                } else if (inputLine.startsWith("FILE TRANSFER START")) {
                    System.out.println(this.consoleprefix+"Starting File Transfer");
                    File outputFile = new File("src\\main\\resources\\"+this.filename);
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    }

                    try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        long totalRead = 0;

                        while (totalRead< filesize && (bytesRead = in.read(buffer)) != -1) {
                            fileOut.write(buffer,0,bytesRead);
                            totalRead += bytesRead;
                        }
                        if (totalRead == filesize) {
                            System.out.println(consoleprefix+"File Transfer complete: "+outputFile.getAbsolutePath());
                            writer.println("FILE TRANSFER SUCCESS");
                        } else {
                            Logger.getGlobal().warning(consoleprefix+"WARNING File Transfer incomplete. Expected: "+filesize+", Recieved: "+totalRead);
                            writer.println("FILE TRANSFER WARNING SUCCESS");
                            stopped = true;

                        }
                    }
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
