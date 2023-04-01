package me.vineer.clansapi.clan.war.maps;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.vineer.clansapi.config.arena.ArenaConfig;
import me.vineer.clansapi.config.arena.ArenaConfigData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public enum MapType {
    TEST(new File(ArenaConfig.data.TEST.schematic));
    private final File file;

    MapType(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public Clipboard getSchematic() throws IOException {
        ClipboardFormat schematicFormat = ClipboardFormats.findByFile(file);
        try(ClipboardReader reader = schematicFormat.getReader(new FileInputStream(file))) {
            Clipboard clipboard = reader.read();
            return clipboard;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IOException();
    }
}
