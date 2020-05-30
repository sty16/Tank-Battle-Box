// dllmain.cpp : 定义 DLL 应用程序的入口点。
#include"pch.h"
#include"messageHandle.h"


void InitializeSocket() {
	WORD w_req = MAKEWORD(2, 2);//版本号
	WSADATA wsadata;
	int err;
	err = WSAStartup(w_req, &wsadata);
	if (err != 0) {
		std::cerr << "初始化套接字库失败！" << std::endl;
	}
	//检测版本号
	if (LOBYTE(wsadata.wVersion) != 2 || HIBYTE(wsadata.wHighVersion) != 2) {
		std::cerr << "套接字库版本号不符！" << std::endl;
		WSACleanup();
	}
}
void sendW(LPWSTR message) {
	//size_t tempBufferSize;
	//setlocale(LC_CTYPE, "zh-CN");
	//wcstombs_s(&tempBufferSize, NULL, 0, message, _TRUNCATE);
	//char* tempBuffer = new char[tempBufferSize];
	//wcstombs_s(&tempBufferSize, tempBuffer, tempBufferSize, message, _TRUNCATE);
	//send(sClient, tempBuffer, tempBufferSize, 0);
	char* buffer = Wtoc(message);
	int bufferSize = 2 * lstrlen(message);
	send(sClient, buffer, bufferSize, 0);
}

void sendA(std::string message) {
	int bufferSize = message.length()+1;
	char* buffer = new char[bufferSize];
	strcpy_s(buffer, bufferSize, message.data());
	send(sClient, buffer, bufferSize, 0);
}

char* Wtoc(LPWSTR message) {
	int bufferSize = 2*lstrlen(message); //存储'\0'作为字符串结尾 error 解决c++与java的大小端问题
	char* buffer = new char[bufferSize];
	char* src = (char*)message;
	for (int i = 0; i < bufferSize; i++) {
		if (i % 2 == 0) {
			buffer[i] = src[i + 1];
		}
		else {
			buffer[i] = src[i - 1];
		}
	}
	return buffer;
}

extern "C" _declspec(dllexport)
void InsertMessage(LPWSTR message) {
	if (send_enable) {
		DWORD result = WaitForSingleObject(gRemainSource, 300);
		if (result != WAIT_OBJECT_0) {
			return;
		}
		EnterCriticalSection(&gInArea);
		LPWSTR temp = new WCHAR[lstrlen(message)];
		lstrcpy(temp, message);
		gMessages[in_index] = temp;
		in_index = (in_index + 1) % gMessageNum;
		LeaveCriticalSection(&gInArea);
		ReleaseSemaphore(gExistMessage, 1, NULL);
	}
}



DWORD WINAPI sendMessageThread(PVOID pvParam) {
	DWORD result = 0;
	while (send_exit == false) {
		WaitForSingleObject(gExistMessage, INFINITE);
		LPWSTR temp = new WCHAR[lstrlen(gMessages[out_index])];
		lstrcpy(temp, gMessages[out_index]);
		 //TODO::sendMessage
		sendW(gMessages[out_index]);
		ReleaseSemaphore(gRemainSource, 1, NULL);
		WaitForSingleObject(&gMutexOut, INFINITE);
		out_index = (out_index + 1) % gMessageNum;
		ReleaseMutex(&gMutexOut);
	}
	return result;
}

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
                     )
{
	DWORD sendpid;
    switch (ul_reason_for_call)
    {
    case DLL_PROCESS_ATTACH:
        gExistMessage = CreateSemaphore(NULL, 0, gMessageNum, NULL);
        gRemainSource = CreateSemaphore(NULL, gMessageNum, gMessageNum, NULL);
        gMutexOut = CreateMutex(NULL, FALSE, L"MutexOut");                    // 创建不被当前线程拥有的互斥锁
        InitializeCriticalSection(&gInArea);                                  //临界区
        InitializeSocket();
        sClient = socket(AF_INET, SOCK_STREAM, 0);
        SOCKADDR_IN serverAddr;
        serverAddr.sin_family = AF_INET;
        serverAddr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");
        serverAddr.sin_port = htons(gPort);
        if (connect(sClient, (SOCKADDR*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
            WSACleanup();
		}
		else {
			CreateThread(NULL, 0, sendMessageThread, NULL, 0, &sendpid);
			send_enable = true;
		}
    case DLL_THREAD_ATTACH:
        break;
    case DLL_THREAD_DETACH:
        break;
    case DLL_PROCESS_DETACH:
		//Sleep(300);
		shutdown(sClient, 1); //  Winsock2.h -> SD_SEND  1 shutdown send operations
		shutdown(sClient, 0); // shutdown receive operations
        send_exit = true;
        closesocket(sClient);
        WSACleanup();
    }
    return TRUE;
}

