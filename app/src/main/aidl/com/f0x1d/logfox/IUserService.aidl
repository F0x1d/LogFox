package com.f0x1d.logfox;

import com.f0x1d.logfox.model.terminal.TerminalResult;
import com.f0x1d.logfox.model.terminal.shizuku.ShizukuTerminalProcess;

interface IUserService {
    void destroy() = 16777114;

    void exit() = 1;

    TerminalResult executeNow(String command) = 2;
    long execute(String command) = 3;

    ParcelFileDescriptor processOutput(long processId) = 4;
    ParcelFileDescriptor processError(long processId) = 5;
    ParcelFileDescriptor processInput(long processId) = 6;

    void destroyProcess(long processId) = 7;
}