#pragma once
#include<Windows.h>
#include "winternl.h"

typedef NTSTATUS(WINAPI* NtQueryInformationProcessFake)(HANDLE, DWORD, PVOID, ULONG, PULONG);

class BaseAPI {
public:
	BaseAPI();
	void moveTo(int x, int y);
	void moveTo(POINT p);
	void LeftClick(int count);
	void KeyPress(WORD keyvalue); // 单键按下
	void KeyPress(WORD key1, WORD key2); //双键同时按下
	void CmdCommand(LPCTSTR command);
	HWND GetProcessHwnd(LPCTSTR className, LPCTSTR windowName);
	LPTSTR GetProcCmd(WORD pid);
private:
	int height;
	int width;
};
