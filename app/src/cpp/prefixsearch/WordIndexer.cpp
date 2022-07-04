
#include "WordIndexer.h"
#include <fstream>
#include <cstring>
#include "Utils.h"
#include "DecodingException.h"

using namespace std;

string convertToString(char* a, int size);

std::vector<std::pair<std::string, Word*>> *WordIndexer::getAllWords(std::string &fileName) {

	ifstream stream;

	vector<pair<string, Word*>> *words = new vector<pair<string, Word*>>;

	stream.open(fileName, ifstream::in | ifstream::binary);

	if (!stream.is_open())
	{
		throw std::ios_base::failure("cent open file");
	}

	char buffer[250];

	do
	{
		stream.read(buffer, 2);

		if (stream.eof()) {
			break;
		}
		
		unsigned short lenght;

		lenght = byteToInt(buffer, 2);

		if (lenght == 0xFFFFFFFF)
		{ 
			break;
		}

        if (lenght == 0x0000FFFF)
		{
			break;
		}

		if (lenght >= 250 -8)
		{
			throw DecodingException((char*)"cant decode correctly");
		}

		int readed_word = lenght + 8;

		stream.read(buffer, readed_word);


		string word_string = convertToString(buffer, lenght);

		if (word_string == "�����") {
			Word* word = new Word;
		}

		int word_start, word_lenght;

		word_start = byteToInt(buffer + lenght, 4);
		word_lenght = byteToInt(buffer + lenght + 4, 4);

		int seekingDist = word_start + word_lenght;

		stream.seekg(seekingDist, ios_base::beg);

		Word* word = new Word;
		word->startIndex = word_start;
		word->lenght = word_lenght;

		words->push_back(pair<string, Word*>(word_string, word));

	} while (!stream.eof());

	stream.close();

	return words;

}

string convertToString(char* a, int size)
{
	
	int i;
	string s = "";
	for (i = 0; i < size; i++) {
		s = s + a[i];
	}
	return s;
}