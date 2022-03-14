#pragma once

#include <exception>

class DecodingException: public std::exception
{
public:
	DecodingException(char* msg);
};

