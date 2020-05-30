#ifndef MESSAGE_HANDLE_H
#define MESSAGE_HANDLE_H
#include<stdlib.h>
#include<iostream>
#include<winsock.h>
#include<string.h>
#pragma comment(lib,"ws2_32.lib")


HANDLE gExistMessage;
HANDLE gRemainSource;
HANDLE gMutexOut;
CRITICAL_SECTION gInArea;
SOCKET sClient;
volatile int in_index = 0;
volatile int out_index = 0;
volatile bool send_exit = false;
volatile bool send_enable = false;
const int gMessageNum = 100;
const int gPort = 8999;
LPWSTR gMessages[gMessageNum];
LPWSTR disconnect;

void InitializeSocket();

void sendW(LPTSTR message);

void sendA(std::string message);

char * Wtoc(LPTSTR message);          // c++ 与 java大小端转换

extern "C" _declspec(dllexport)
void InsertMessage(LPWSTR message);

extern "C" _declspec(dllexport)
void InsertMessageA(std::string message);

DWORD WINAPI sendMessageThread(PVOID pvParam);

#endif // !MESSAGE_HANDLE_H
