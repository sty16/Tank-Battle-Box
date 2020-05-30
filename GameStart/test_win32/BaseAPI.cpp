#include"BaseAPI.h"

BaseAPI::BaseAPI() {
	height = GetSystemMetrics(SM_CYSCREEN);
	width = GetSystemMetrics(SM_CXSCREEN);
}

void BaseAPI::moveTo(int x, int y) {
    INPUT input;
    memset(&input, 0, sizeof(input));
    input.type = INPUT_MOUSE;
    input.mi.dx = x * 65535 / width;
    input.mi.dy = y * 65535 / height;
    input.mi.dwFlags = MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_MOVE;
    SendInput(1, &input, sizeof(INPUT));
}

void BaseAPI::moveTo(POINT p) {
    moveTo(p.x, p.y);
}
 
void BaseAPI::LeftClick(int count) {
    POINT pos;
    GetCursorPos(&pos);
    INPUT input;
    ZeroMemory(&input, sizeof(input));
    input.type = INPUT_MOUSE;
    input.mi.dx = pos.x * 65535 / width;
    input.mi.dy = pos.y * 65535 / height;
    input.mi.dwFlags = MOUSEEVENTF_LEFTDOWN;
    for (int i = 0; i < count; i++) {
        SendInput(1, &input, sizeof(INPUT));
        Sleep(100);
        input.mi.dwFlags = MOUSEEVENTF_LEFTUP;
        SendInput(1, &input, sizeof(INPUT));
        Sleep(100);
    }
}

void BaseAPI::KeyPress(WORD keyvalue) {
    INPUT input[2];
    memset(input, 0, sizeof(input));
    input[0].type = INPUT_KEYBOARD;
    input[0].ki.wVk = keyvalue;
    input[1].type = INPUT_KEYBOARD;
    input[1].ki.wVk = keyvalue;
    input[1].ki.dwFlags = KEYEVENTF_KEYUP;
    SendInput(2, input, sizeof(INPUT));
}

void BaseAPI::KeyPress(WORD key1, WORD key2) {
    INPUT input[4];
    memset(input, 0, sizeof(input));
    input[0].type = INPUT_KEYBOARD;
    input[0].ki.wVk = key1;
    input[1].type = INPUT_KEYBOARD;
    input[1].ki.wVk = key2;
    input[2].type = INPUT_KEYBOARD;
    input[2].ki.wVk = key1;
    input[2].ki.dwFlags = KEYEVENTF_KEYUP;
    input[3].type = INPUT_KEYBOARD;
    input[3].ki.wVk = key2;
    input[3].ki.dwFlags = KEYEVENTF_KEYUP;
    SendInput(4, input, sizeof(INPUT));
}

void BaseAPI::CmdCommand(LPCTSTR command) {
    STARTUPINFO si;
    ZeroMemory(&si, sizeof(STARTUPINFO));
    si.cb = sizeof(STARTUPINFO);
    si.wShowWindow = SW_HIDE;
    PROCESS_INFORMATION pi;
    TCHAR commandline[MAX_PATH];
    lstrcpy(commandline, command);
    BOOL bRet = CreateProcess(NULL, commandline, NULL, NULL, FALSE, CREATE_NO_WINDOW, NULL, NULL, &si, &pi);
    if (bRet)
    {
        WaitForSingleObject(pi.hProcess, INFINITE);
        CloseHandle(pi.hThread);
        CloseHandle(pi.hProcess);
    }
}

HWND BaseAPI::GetProcessHwnd(PCTSTR className, LPCTSTR windowName)
{
    HWND client = FindWindow(className, windowName);
    return client;
}

LPTSTR BaseAPI::GetProcCmd(WORD pid)                   //获取进程命令行参数
{
    NtQueryInformationProcessFake ntQ = NULL;
    HANDLE hproc = OpenProcess(PROCESS_ALL_ACCESS, FALSE, pid);
    LPTSTR result = NULL;
    if (INVALID_HANDLE_VALUE != hproc) {
        HANDLE hnewdup = NULL;
        PEB peb;
        RTL_USER_PROCESS_PARAMETERS upps;
        WCHAR buffer[MAX_PATH] = { NULL };
        HMODULE hm = LoadLibrary(L"Ntdll.dll");
        if (hm != NULL) {
            ntQ = (NtQueryInformationProcessFake)GetProcAddress(hm, "NtQueryInformationProcess");
            if (DuplicateHandle(GetCurrentProcess(), hproc, GetCurrentProcess(), &hnewdup, 0, FALSE, DUPLICATE_SAME_ACCESS)) {
                PROCESS_BASIC_INFORMATION pbi;
                NTSTATUS isok = ntQ(hnewdup, 0, (PVOID)&pbi, sizeof(PROCESS_BASIC_INFORMATION), 0);
                if (BCRYPT_SUCCESS(isok)) {
                    if (ReadProcessMemory(hnewdup, pbi.PebBaseAddress, &peb, sizeof(PEB), 0))
                        if (ReadProcessMemory(hnewdup, peb.ProcessParameters, &upps, sizeof(RTL_USER_PROCESS_PARAMETERS), 0)) {
                            WCHAR* buffer = new WCHAR[upps.CommandLine.Length + 1];
                            ZeroMemory(buffer, (upps.CommandLine.Length + 1) * sizeof(WCHAR));
                            ReadProcessMemory(hnewdup, upps.CommandLine.Buffer, buffer, upps.CommandLine.Length, 0);
                            result = buffer;
                        }
                }
                CloseHandle(hnewdup);
            }
        }
        CloseHandle(hproc);
    }
    return result;
}