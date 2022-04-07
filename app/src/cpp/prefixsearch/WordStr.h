#pragma once
struct Word
{
	int startIndex;
	int lenght;
};

struct ResultWord {
	std::string key_word;
	int distanse = 0;
	Word* word = nullptr;
};