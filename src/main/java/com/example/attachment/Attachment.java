
package com.example.attachment;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Attachment {
    @Id
    @GeneratedValue
    private Long id;

    private String originalName;
    private String storedKey;

    @OneToMany(mappedBy = "attachment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachmentMapping> mappings = new ArrayList<>();

    public Attachment() {}
    public Attachment(String originalName, String storedKey) {
        this.originalName = originalName;
        this.storedKey = storedKey;
    }

    public Long getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getStoredKey() { return storedKey; }
}
