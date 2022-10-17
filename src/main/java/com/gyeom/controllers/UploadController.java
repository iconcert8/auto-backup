package com.gyeom.controllers;

import com.gyeom.domain.AppConfiguration;
import com.gyeom.domain.FileRepository;
import com.gyeom.domain.usecase.BackupCountManageUseCase;
import com.gyeom.domain.usecase.FileUploadUseCase;
import com.gyeom.domain.usecase.FileZipUseCase;
import com.gyeom.infrastructure.LocalFileRepository;
import com.gyeom.model.DirectoryPath;
import com.gyeom.model.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UploadController {
    private final static Logger log = LoggerFactory.getLogger(UploadController.class);

    private final FileRepository backUpFileRepository;
    private Thread uploadLoopThread;
    private volatile boolean uploadLoopActive;

    public UploadController(FileRepository backUpFileRepository) {
        this.backUpFileRepository = backUpFileRepository;
        this.uploadLoopActive = true;
    }

    public Future<Boolean> uploadAsync(DirectoryPath dest, List<FileVO> fileVOS) {
        return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                FileVO zipFileVO = new FileZipUseCase().call(fileVOS);
                return new FileUploadUseCase(backUpFileRepository).call(dest, zipFileVO);
            }
        });
    }

    public void upload(DirectoryPath dest, List<FileVO> fileVOS) {
        FileVO zipFileVO = new FileZipUseCase().call(fileVOS);
        new FileUploadUseCase(backUpFileRepository).call(dest, zipFileVO);
        new BackupCountManageUseCase(backUpFileRepository).call(dest);
        new BackupCountManageUseCase(new LocalFileRepository())
                .call(
                        DirectoryPath.of(
                                new File(AppConfiguration.getInstance().getTempZipFolder()
                                )
                        )
                );
    }

    /**
     * Functions that upload regularly
     */
    public void startUpload(DirectoryPath dest, List<FileVO> fileVOS, int periodMillisecond) throws InterruptedException {
        if (uploadLoopThread != null) {
            log.warn("Uploading loop is already running.");
            return;
        }

        //TODO: check period min value
        uploadLoopActive = true;
        uploadLoopThread = new Thread(
                () -> {
                    while (uploadLoopActive) {
                        upload(dest, fileVOS);
                        try {
                            Thread.sleep(periodMillisecond);
                        } catch (InterruptedException e) {
                            stopUpload();
                        }
                    }
                }
        );
        uploadLoopThread.start();
        uploadLoopThread.join();
        uploadLoopThread = null;

//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                upload(dest, fileVOS);
//            }
//        };
//
//        timer.schedule(timerTask, 0, period);
    }

    public void stopUpload() {
        uploadLoopActive = false;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (uploadLoopThread != null) {
                    uploadLoopThread.interrupt();
                }
            }
        }, 3000);
    }
}
