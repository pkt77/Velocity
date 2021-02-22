package com.velocitypowered.proxy.network.packet.serverbound;

import com.google.common.base.MoreObjects;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.network.ProtocolUtils;
import com.velocitypowered.proxy.network.packet.Packet;
import com.velocitypowered.proxy.network.packet.PacketHandler;
import com.velocitypowered.proxy.network.packet.PacketReader;
import com.velocitypowered.proxy.network.packet.PacketWriter;
import io.netty.buffer.ByteBuf;

public class ServerboundEncryptionResponsePacket implements Packet {
  public static final PacketReader<ServerboundEncryptionResponsePacket> DECODER = (buf, version) -> {
    final byte[] sharedSecret;
    final byte[] verifyToken;
    if (version.gte(ProtocolVersion.MINECRAFT_1_8)) {
      sharedSecret = ProtocolUtils.readByteArray(buf, 256);
      verifyToken = ProtocolUtils.readByteArray(buf, 128);
    } else {
      sharedSecret = ProtocolUtils.readByteArray17(buf);
      verifyToken = ProtocolUtils.readByteArray17(buf);
    }
    return new ServerboundEncryptionResponsePacket(sharedSecret, verifyToken);
  };
  public static final PacketWriter<ServerboundEncryptionResponsePacket> ENCODER = PacketWriter.deprecatedEncode();

  private final byte[] sharedSecret;
  private final byte[] verifyToken;

  private ServerboundEncryptionResponsePacket(final byte[] sharedSecret, final byte[] verifyToken) {
    this.sharedSecret = sharedSecret;
    this.verifyToken = verifyToken;
  }

  @Override
  public void encode(ByteBuf buf, ProtocolVersion version) {
    if (version.gte(ProtocolVersion.MINECRAFT_1_8)) {
      ProtocolUtils.writeByteArray(buf, sharedSecret);
      ProtocolUtils.writeByteArray(buf, verifyToken);
    } else {
      ProtocolUtils.writeByteArray17(sharedSecret, buf, false);
      ProtocolUtils.writeByteArray17(verifyToken, buf, false);
    }
  }

  @Override
  public boolean handle(PacketHandler handler) {
    return handler.handle(this);
  }

  public byte[] getSharedSecret() {
    return sharedSecret.clone();
  }

  public byte[] getVerifyToken() {
    return verifyToken.clone();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("sharedSecret", this.sharedSecret)
      .add("verifyToken", this.verifyToken)
      .toString();
  }
}
