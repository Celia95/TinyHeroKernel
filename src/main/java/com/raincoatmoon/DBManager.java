package com.raincoatmoon;

import com.pengrad.telegrambot.model.User;
import com.raincoatmoon.TimeManager.CronTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static Connection connection = null;

    public static void init() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager
                        .getConnection("jdbc:postgresql://" + EnvVariables.DB_HOST + ":"
                                        + EnvVariables.DB_PORT + "/" + EnvVariables.DB_NAME,
                                EnvVariables.DB_USER, EnvVariables.DB_PASS);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
            System.out.println("DB connection: OK");
        }
    }

    public static boolean registerUser(User user) {
        // We recommend to store some information about your users (just in case).
        // This method is called each time a user sends a message.
        String sql = "INSERT INTO users(id, user_name, first_name, last_name) " +
                "VALUES('" + user.id() + "', '" + user.username() + "', '" + user.firstName() + "', '" + user.lastName() + "')";
        return execUpdate(sql);
    }

    public static boolean checkPermissions(User user, String permission) {
        // This method is never used. you can use it for restricted commands.
        String sql = "SELECT count(id) FROM " + permission + " WHERE id = " + user.id();
        ResultSet set = execQuery(sql);
        try {
            if (set != null && set.next()) {
                return set.getInt("count") > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<CronTask> getCronTasks(int last, int now) {
        String sql = now >= last? "SELECT user_id, time, cmd, info FROM cron_events WHERE time BETWEEN " + last + " AND " + now + ";":
                "SELECT user_id, time, cmd FROM cron_events WHERE time >= " + last + " OR time <= " + now + ";";
        ArrayList<CronTask> res = new ArrayList<>();
        ResultSet set = execQuery(sql);
        try {
            while (set.next()) {
                CronTask cronTask = new CronTask(set.getInt("time"), set.getInt("user_id"), set.getString("cmd"), set.getString("info"));
                res.add(cronTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<CronTask> getCronTasks(User user) {
        String sql = "SELECT user_id, time, cmd, info FROM cron_events WHERE user_id = " + user.id() + " ORDER BY time;";
        ArrayList<CronTask> res = new ArrayList<>();
        ResultSet set = execQuery(sql);
        try {
            while (set.next()) {
                CronTask cronTask = new CronTask(set.getInt("time"), set.getInt("user_id"), set.getString("cmd"), set.getString("info"));
                res.add(cronTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean insertCronTask(CronTask cronTask) {
        String sql = "INSERT INTO cron_events(user_id, time, cmd, info) VALUES("
                + cronTask.getUserID() + ", " + cronTask.getTime() + ", '" + cronTask.getCmd() + "', '" + cronTask.getInfo() + "')";
        return execUpdate(sql);
    }

    public static boolean deleteCronTask(CronTask cronTask) {
        String sql = "DELETE FROM cron_events WHERE user_id = "
                + cronTask.getUserID() + " AND time = " + cronTask.getTime() + " AND cmd = '" + cronTask.getCmd() + "'";
        return execUpdate(sql);
    }

    public static List<Object> getObjectsFromDB(int payloadID) {
        String sql = "Here you can type your SELECT query";
        ArrayList<Object> res = new ArrayList<>();
        ResultSet set = execQuery(sql);
        try {
            while (set.next()) { // Once you have called execQuery, you can iterate over the result set
                Object payload = new Object(); //(set.getInt("user_id"), set.getString("name"), set.getString("url"));
                res.add(payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static boolean insertObjectsInDB(Object payload) {
        String sql = "Here you can type your INSERT query using the data stored in your payload object";
        return execUpdate(sql);
    }

    public static boolean updateObjectsInDB(Object payload) {
        String sql = "Here you can type your UPDATE query using the data stored in your payload object";
        return execUpdate(sql);
    }

    public static boolean deleteObjectsInDB(Object payload) {
        String sql = "Here you can type your DELETE query using the data stored in your payload object";
        return execUpdate(sql);
    }

    private static boolean execUpdate(String sql) {
        try {
            Statement stm = connection.createStatement();
            stm.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    private static ResultSet execQuery(String sql) {
        try {
            Statement stm = connection.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            return rs;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }
}
