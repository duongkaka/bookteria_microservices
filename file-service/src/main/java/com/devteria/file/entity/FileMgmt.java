package com.devteria.file.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collation = "file_mgmt")
@FieldDefaults(level = AccessLevel.PACKAGE)
public class FileMgmt {
    @MongoId
    String id;
    String ownerID;

    String contentType;
    long size;
    String md5Checksum;
    String path;


}
