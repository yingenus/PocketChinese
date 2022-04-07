#pragma once

#include "PrefixNode.h"

#include <string>
#include <vector>

class PrefixTree {

public:
	PrefixTree();
	~PrefixTree();

	void add(std::string& key, Word& word);
	std::vector<ResultWord*>* get(std::string &key);
	std::vector<ResultWord*>* getSimular(std::string& key, int distanse);
	int mu_size();

private:

	Node *root = nullptr;
	
	std::vector<ResultWord*>* makeUnike(std::vector<ResultWord*> *ununikeVector);
	
};