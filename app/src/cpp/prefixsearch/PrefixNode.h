#pragma once

#include <string>
#include <vector>

#include "WordStr.h"




class Node {

	struct Link {
		char key;
		Node* node;
	};

public:
	Node();
	~Node();

	void add(std::string& key, Word &word);
	std::vector<ResultWord*>* get(std::string& key);
	std::vector<ResultWord*>* getSimilar(std::string& key, int distanse, bool was_insert, bool was_skip);

	int my_size();

private:
	int array_lenght = 0;
	Link** links = nullptr;
	Word* leaf = nullptr;

	Node* findNode(char key);
	bool isContainNode(char key);
	std::string getSubKey(std::string& key);
};

