package com.fassykite.polarisx.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.fassykite.polarisx.PolarisX;

public class PacketListener extends PacketAdapter {

    public PacketListener(PolarisX plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        // Заглушка для ProtocolLib
    }

    public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }
}