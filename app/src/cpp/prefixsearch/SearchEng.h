#pragma once
#include<string>
#include<vector>
#include "SentenseExtractor.h"
#include "Distanse.h"
#include "PrefixTree.h"

struct Candidate {
	SentenseExtractor::VarianteWord word;
	int involc_words;
	int common_distanse;
};

class SearchEng
{
public:
	SearchEng(PrefixTree *tree, SentenseExtractor *extractor, Distanse* distanse);
	~SearchEng();
	std::vector<int> *find(std::string query); // возвращает отсртированыый масив id слов
	void setDistanse(Distanse& distanse);
private:
	

	PrefixTree* tree;
	SentenseExtractor* extractor;
	Distanse* distanse;

	std::vector<Candidate*>* findForWord(std::string word);
	unsigned int getHash(SentenseExtractor::VarianteWord &word);

};

