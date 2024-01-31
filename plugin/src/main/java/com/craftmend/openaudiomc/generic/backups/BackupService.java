package com.craftmend.openaudiomc.generic.backups;

import com.craftmend.openaudiomc.generic.environment.MagicValue;
import com.craftmend.openaudiomc.generic.logging.OpenAudioLogger;
import com.craftmend.openaudiomc.generic.service.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

public class BackupService extends Service {

    private boolean madeBackup = false;

    public void makeBackup(boolean force) {
        if (!force) {
            if (madeBackup) return;
            madeBackup = true;
        }
        OpenAudioLogger.toConsole("Making a backup of your database, config, and data.yml");

        // check backups dir
        File backupRootDirectory = new File(MagicValue.STORAGE_DIRECTORY.get(File.class), File.pathSeparator + "backups");
        if (!backupRootDirectory.exists()) {
            backupRootDirectory.mkdir();
        }

        long unixTime = Instant.now().getEpochSecond();
        // create current backup dir
        File backupDir = new File(backupRootDirectory, File.pathSeparator + "backup-" + unixTime);
        if (!backupDir.exists()) {
            backupDir.mkdir();
        } else {
            OpenAudioLogger.toConsole("Backup directory already exists");
        }

        try {
            Files.copy(
                    new File(MagicValue.STORAGE_DIRECTORY.get(File.class), "config.yml").toPath(),
                    new File(backupDir, "config.yml").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            OpenAudioLogger.toConsole("Failed to backup config.yml");
            e.printStackTrace();
        }

        try {
            Files.copy(
                    new File(MagicValue.STORAGE_DIRECTORY.get(File.class), "data.yml").toPath(),
                    new File(backupDir, "data.yml").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            OpenAudioLogger.toConsole("Failed to backup data.yml");
            e.printStackTrace();
        }

        try {
            Files.copy(
                    new File(MagicValue.STORAGE_DIRECTORY.get(File.class), "database.db").toPath(),
                    new File(backupDir, "database.db").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            // legacy, allowed to fail
        }

        try {
            Files.copy(
                    new File(MagicValue.STORAGE_DIRECTORY.get(File.class), "storm.db").toPath(),
                    new File(backupDir, "storm.db").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            OpenAudioLogger.toConsole("Failed to backup storm.db");
            e.printStackTrace();
        }

    }

}
