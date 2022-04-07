
#include "SentenseExtractor.h"
#include <fstream>
#include <cstring>
#include "Utils.h"

using namespace std;

SentenseExtractor::SentenseExtractor() {

}

int SentenseExtractor::init(string &file_name) {

	ifstream stream;

	stream.open(file_name);

	if (!stream.is_open())
	{
		return -1;
	}

	filename = file_name;

	stream.close();

	return 0;

}

std::vector<SentenseExtractor::ResultVarianteWords*>* SentenseExtractor::extract(std::vector<ResultWord*>& word_vector) {
	ifstream stream;

	stream.open(filename, ifstream::in | ifstream::binary);

	if (!stream.is_open())
	{
		throw std::ios_base::failure("cent open file");
	}

	vector<ResultVarianteWords*>* results = new vector<ResultVarianteWords*>;

	char buffer[120];

	results->reserve(word_vector.size());

	for (int word_index = 0; word_index < word_vector.size(); word_index ++) {

		Word* word = word_vector.at(word_index)->word;

		vector<VarianteWord> *variants = new vector<VarianteWord>;
		
		int items = word->lenght / 6;

		int buyts = word->lenght;

		stream.seekg(word->startIndex, ios_base::beg);

		do
		{
			int read;

			if (buyts > 120){
				read = 120;
			}
			else{
				read = buyts;
			}

			buyts -= read;

			stream.read(buffer, read);

			int r_size; 

			VarianteWord** readed = getVarintWords(buffer, read, &r_size);

			int variants_size = variants->size();

			variants->resize(r_size + variants_size);

			for (int i = 0; i < r_size; i++) {
				(*variants)[i + variants_size] = *(readed)[i];
			}
			
			delete [] readed;

		} while (buyts > 0);

		ResultVarianteWords* result = new ResultVarianteWords;

		result->word_result = word_vector[word_index];
		result->varianteWords = variants;

		results->push_back(result);
	}

	stream.close();

	return results;
}

SentenseExtractor::VarianteWord* SentenseExtractor::getVarintWord(char* word_code) {
	unsigned int id_plus;
	unsigned int weight;
	
	id_plus = byteToInt(word_code, 4);
	weight = byteToInt(word_code + 4, 2);

	int id = id_plus >> 10;
	int sub_id = id_plus & 0b1111111111;

	VarianteWord *word = new VarianteWord;

	word->id = id;
	word->index = sub_id;
	word->weight = weight;

	return word;
}

SentenseExtractor::VarianteWord** SentenseExtractor::getVarintWords(char* buff, int buff_size, int* r_size) {
	
	int words_count = buff_size / 6;//count

	VarianteWord** words = new VarianteWord*[words_count];

	for (int i = 0; i < words_count; i++) {
		VarianteWord *word = getVarintWord(buff + (i * 6));
		words[i] = word;
	}
	*r_size = words_count;
	return words;
}

SentenseExtractor::~SentenseExtractor() {
	//delete &filename;
}