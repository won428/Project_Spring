
package com.example.attachment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentRepository attachmentRepo;
    private final AttachmentMappingRepository mappingRepo;

    public AttachmentController(AttachmentRepository attachmentRepo, AttachmentMappingRepository mappingRepo) {
        this.attachmentRepo = attachmentRepo;
        this.mappingRepo = mappingRepo;
    }

    @GetMapping("/test")
    public String testSave() {
        Attachment a = new Attachment("spring17.pdf", "stored-key-17");
        attachmentRepo.save(a);
        mappingRepo.save(new AttachmentMapping(a, TargetType.ASSIGNMENT, 123L));
        return "Saved with Spring 17"; // ✅ 이게 나와야 해요!
    }


    @GetMapping
    public List<Attachment> getByTarget(@RequestParam TargetType type, @RequestParam Long id) {
        return mappingRepo.findByTargetTypeAndTargetId(type, id)
                .stream().map(AttachmentMapping::getAttachment).toList();
    }
}
