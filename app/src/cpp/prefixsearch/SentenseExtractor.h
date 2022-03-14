#pragma once

#include <string>
#include <vector>
#include "WordStr.h"



class SentenseExtractor {

public:

	struct VarianteWord {
		int id = -1;
		int index = -1;
		int weight = -1;
	};

	struct ResultVarianteWords {
		ResultWord* word_result;
		std::vector<VarianteWord>* varianteWords;
		
	};

	SentenseExtractor();
	~SentenseExtractor();

	std::vector<ResultVarianteWords*>* extract(std::vector<ResultWord*>& word_vector);

	int init(std::string &file_name);

private:
	std::string filename;

	VarianteWord* getVarintWord(char *word_code);
	VarianteWord** getVarintWords(char* buff, int buff_size, int* r_size);
};
