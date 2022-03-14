#pragma once

#include<string>
#include<vector>
#include "SearchEng.h"

class EngineFacade
{
public:

	enum language
	{
		RUSSIAN, PINYIN, DEFAULT
	};

	EngineFacade();
	void init(std::string file_name);
	std::vector<int>* find(std::string query);
	void setLang(language lang);
	~EngineFacade();

private:

	language active_leng = DEFAULT;
	SearchEng* searchEngin = nullptr;

};

