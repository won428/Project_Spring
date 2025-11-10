package com.secondproject.secondproject.publicMethod;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VidLengthGetter {
    public static int getDurationInSec(MultipartFile file) {
        File temptFile = null;
        SeekableByteChannel channel = null;

        try {
            temptFile = File.createTempFile("jcodec-temp-", file.getOriginalFilename());
            file.transferTo(temptFile);
            channel = NIOUtils.readableChannel(temptFile);

            FrameGrab grab = FrameGrab.createFrameGrab(channel);

            double totalDur = grab.getVideoTrack().getMeta().getTotalDuration();
            return (int) Math.round(totalDur);

        } catch (IOException | JCodecException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 4. 임시 파일 및 채널 정리 (매우 중요)
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (temptFile != null && temptFile.exists()) {
                temptFile.delete();
            }
        }

    }

}
