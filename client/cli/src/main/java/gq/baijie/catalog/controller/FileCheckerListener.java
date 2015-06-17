package gq.baijie.catalog.controller;

import java.io.IOException;

import javax.annotation.Nonnull;

import gq.baijie.catalog.entity.Hash;
import gq.baijie.catalog.entity.RegularFile;
import gq.baijie.catalog.usecase.CheckFileInformation;

public class FileCheckerListener extends CheckFileInformation.SimpleFileCheckerListener {

    private final boolean failFast;

    //TODO private State state = State.IDLE; for ↓

    private boolean allFileOk = true;

    public FileCheckerListener(boolean failFast) {
        this.failFast = failFast;
    }

    public boolean isAllFileOk() {
        return allFileOk;
    }

    @Nonnull
    @Override
    public CheckFileInformation.CheckResult onFileChecked(@Nonnull RegularFile file,
            @Nonnull Hash[] realHashs, boolean fileOk) {
        if (fileOk) {
            return CheckFileInformation.CheckResult.CONTINUE;
        } else {
            System.out.println("File corrupted: " + file.getPath());
            allFileOk = false;
            if (failFast) {
                return CheckFileInformation.CheckResult.TERMINATE;
            } else {
                return CheckFileInformation.CheckResult.CONTINUE;
            }
        }
    }

    @Nonnull
    @Override
    public CheckFileInformation.CheckResult onCheckFileFailed(@Nonnull RegularFile file,
            @Nonnull IOException exception) {
        exception.printStackTrace();
        allFileOk = false;
        if (failFast) {
            return CheckFileInformation.CheckResult.TERMINATE;
        } else {
            return CheckFileInformation.CheckResult.CONTINUE;
        }
    }

}
