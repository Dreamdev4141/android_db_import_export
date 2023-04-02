package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class DataSource {
    private SQLiteDatabase db;
    private final DataBaseHelper dbHelper;

    public DataSource(Context context, String dbPath) {
        dbHelper = new DataBaseHelper(context, dbPath);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private String getFixedLengthString(String name, int fixedLength, boolean isPrefix) {
        if (name == null) {
            name = "";
        }
        if (name.length() > fixedLength) {
            name = name.substring(0, fixedLength);
        }
        if (isPrefix) {
            StringBuilder result = new StringBuilder();
            for (int i = name.length(); i < fixedLength; i++)
                result.append(" ");
            result.append(name);
            return result.toString();
        } else {
            return String.format("%1$-" + fixedLength + "s", name);
        }

    }

    public void exportToTxt(String txtFilePath, String tableName) throws Exception {

        // Delete the file if exists.
        File file = new File(txtFilePath);
        if (file.exists()) {
            boolean ok = file.delete();
            if (!ok) {
                throw new Exception("Failed to delete the txt file.");
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        // Get all records from table.
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

        // Array of max lengths for generating fixed length strings.
        int[] maxLengths = new int[cursor.getColumnCount()];

        while (cursor.moveToNext()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < cursor.getColumnCount(); i++) {

                // Get the max length of the column if it doesn't exist.
                if (maxLengths[i] == 0) {
                    Cursor maxLengthCursor = db.rawQuery(
                            String.format("SELECT MAX(LENGTH(%s)) + 5 as max_length FROM " + tableName, cursor.getColumnName(i)),
                            null
                    );
                    maxLengthCursor.moveToNext();
                    maxLengths[i] = maxLengthCursor.getInt(0);
                    maxLengthCursor.close();
                }

                // Make a fixed length string.
                if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
                    stringBuilder.append(getFixedLengthString(cursor.getString(i).trim(), maxLengths[i], false));
                } else {
                    stringBuilder.append(getFixedLengthString(String.valueOf(cursor.getInt(i)), maxLengths[i], true));
                }

                // Append a separator.
                stringBuilder.append('|');
            }

            // Append a line break.
            stringBuilder.append("\r\n");

            // Write a line to the txt file.
            fileOutputStream.write(stringBuilder.toString().getBytes());
        }

        cursor.close();
        fileOutputStream.close();

    }

    public void importFromText(String txtFilePath, String tableName) throws Exception {

        // Checks whether file exists.
        File file = new File(txtFilePath);
        if (!file.exists()) {
            throw new Exception("File doesn't exist.");
        }

        // Ready to read the file
        FileInputStream fileInputStream = new FileInputStream(file);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));

        String line = "";

        String seperator = "|";

        // Read line by line.
        while ((line = bufferedReader.readLine()) != null) {

            // Remove all whitespaces.
            line = Pattern.compile("\\p{javaSpaceChar}{2,}").matcher(line).replaceAll("");

            // If it ends with a separator, eliminates it.
            if (line.endsWith(seperator)) {
                line = line.substring(0, line.length() - 1);
            }

            // Prepare values array for insert.
            line = "'" + line.replace("|", "\',\'") + "'";

            // Prepare sql query for insert.
            String insertSql = String.format("INSERT INTO %s VALUES (%s);", tableName, line);

            // Execute the sql.
            db.execSQL(insertSql);
        }

        bufferedReader.close();
        dataInputStream.close();
        fileInputStream.close();
    }
}
