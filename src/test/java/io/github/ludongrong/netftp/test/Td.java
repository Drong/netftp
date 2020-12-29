package io.github.ludongrong.netftp.test;

import java.util.List;
import java.util.stream.Collectors;

import io.github.ludongrong.netftp.FtperConfig;
import io.github.ludongrong.netftp.FtperFile;
import io.github.ludongrong.netftp.FtperConfig.ProtocolEnum;
import io.github.ludongrong.netftp.FtperConfig.TypeEnum;
import io.github.ludongrong.netftp.FtperException;
import io.github.ludongrong.netftp.FtperFactory;

public class Td {

    public static void main(String[] args) throws FtperException {
        FtperConfig ftperConfig = FtperConfig.withHost("10.210.53.143").withPort(21).withUsername("omcrftp")
                .withPassword("omcrftp.DTM123").withPasvMode(true).withProtocol(ProtocolEnum.ftp).withType(TypeEnum.ftp4j).build();
        List<FtperFile> ftperFiles = FtperFactory.createFtper(ftperConfig).listFile("/export/home/omcrftp/FJ/WX/DT/OMCR");
        ftperFiles.stream().filter(t -> {
            return t.getName().contains("move");
        }).collect(Collectors.toList());
    }
}
