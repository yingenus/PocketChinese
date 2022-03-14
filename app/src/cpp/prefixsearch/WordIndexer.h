#pragma once

#include<vector>
#include<string>
#include"PrefixTree.h"
#include <unordered_map>

class WordIndexer
{
public:
	std::vector<std::pair<std::string, Word*>> *getAllWords(std::string &fileName);
};

