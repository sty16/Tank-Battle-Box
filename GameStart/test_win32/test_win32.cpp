// test_win32.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//
#include"BaseAPI.h"
#include <iostream>
#include<Windows.h>
#include <Pathcch.h>
#include<winerror.h>
#include<Shlobj.h>
#include<atlstr.h>
#pragma comment(lib,"Pathcch.lib")

int main()
{
    BaseAPI api;
    TCHAR DataDir[MAX_PATH];
    if (SUCCEEDED(SHGetFolderPath(NULL, CSIDL_APPDATA, NULL, 0, DataDir))) {
        PathCchAppend(DataDir, sizeof(DataDir), L"Tencent\\QQMicroGameBox\\坦克大战");
    }
    int result = (int)ShellExecute(NULL, L"open", DataDir, NULL, NULL, SW_SHOW);
    TCHAR* DocDir = new TCHAR[MAX_PATH];
    if (SUCCEEDED(SHGetFolderPath(NULL, CSIDL_MYDOCUMENTS, NULL, 0, DocDir))) {
    }
    TCHAR* commandline = new TCHAR[MAX_PATH];
    lstrcpy(commandline, DocDir);
    lstrcat(commandline, L"\\tkdz\\Tank.exe ID:");
    while (true)
    {
        HWND hwnd = FindWindow(L"#32770", L"坦克大战登陆器");
        if (hwnd != NULL)
        {
            SendMessage(hwnd, WM_SYSCOMMAND, SC_MINIMIZE, 0);
            DWORD pid;
            GetWindowThreadProcessId(hwnd, &pid);
            LPTSTR cmdParam = api.GetProcCmd(pid);
            if (cmdParam != NULL)
            {
                SendMessage(hwnd, WM_CLOSE, 0, 0);
                CString target(cmdParam);
                int pos1 = target.Find(L"ID=", 0);
                int pos2 = target.Find(L"Key=", 0);
                CString id = target.Mid(pos1 + 3, 32);
                CString key = target.Mid(pos2 + 4, 32);
                lstrcat(commandline, id);
                lstrcat(commandline, L",Key:");
                lstrcat(commandline, key);
                lstrcat(commandline, L",PID:10,serverId:7");
                break;
            }
        }
        Sleep(500);
    }
    TCHAR* injectFile = new TCHAR[MAX_PATH];
    lstrcpy(injectFile, DocDir);
    lstrcat(injectFile, L"\\tkdz\\Tank_Data\\Managed\\Assembly-CSharp-Change.dll");
    TCHAR* sourceFile = new TCHAR[MAX_PATH];
    lstrcpy(sourceFile, DocDir);
    lstrcat(sourceFile, L"\\tkdz\\Tank_Data\\Managed\\Assembly-CSharp.dll");
    CopyFile(injectFile, sourceFile, false);
    STARTUPINFO si = { sizeof(si) };
    PROCESS_INFORMATION pi;
    BOOL tank = CreateProcess(NULL, commandline, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi);
    CloseHandle(pi.hThread);
    CloseHandle(pi.hProcess);
}

// 运行程序: Ctrl + F5 或调试 >“开始执行(不调试)”菜单
// 调试程序: F5 或调试 >“开始调试”菜单

// 入门使用技巧: 
//   1. 使用解决方案资源管理器窗口添加/管理文件
//   2. 使用团队资源管理器窗口连接到源代码管理
//   3. 使用输出窗口查看生成输出和其他消息
//   4. 使用错误列表窗口查看错误
//   5. 转到“项目”>“添加新项”以创建新的代码文件，或转到“项目”>“添加现有项”以将现有代码文件添加到项目
//   6. 将来，若要再次打开此项目，请转到“文件”>“打开”>“项目”并选择 .sln 文件
