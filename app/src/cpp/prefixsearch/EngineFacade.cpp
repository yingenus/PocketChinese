
#include <stdexcept>
#include "EngineFacade.h"
#include "WordIndexer.h"


using namespace std;

EngineFacade::EngineFacade() {

}

void EngineFacade::init(string file_name) {
	if (searchEngin == nullptr){
        Distanse* distanse;
        WordIndexer indexr;
        PrefixTree *tree = new PrefixTree;
        SentenseExtractor* extractor = new SentenseExtractor();

        if (active_leng == RUSSIAN) {
            distanse = dynamic_cast<Distanse*>(new RusDistanse);
        }
        else if (active_leng == PINYIN){
            distanse = dynamic_cast<Distanse*>(new PinDistanse);
        }
        else{
            distanse = dynamic_cast<Distanse*>(new Distanse);
        }

        int init = extractor->init(file_name);

        std::vector<std::pair<std::string, Word*>>* words = indexr.getAllWords(file_name);

        for (int i = 0; i < words->size(); i++) {
            std::pair<std::string, Word*> word = words->at(i);

            tree->add(word.first, *word.second);
        }

        searchEngin = new SearchEng(tree, extractor, distanse);

        delete words;
	}
}

void EngineFacade::setLang(language lang) {
    if (active_leng != lang) {
        active_leng = lang;

        if (searchEngin != nullptr)
        {
            Distanse* distanse;
            if (active_leng == RUSSIAN) {
                distanse = dynamic_cast<Distanse*>(new RusDistanse);
            }
            else if (active_leng == PINYIN) {
                distanse = dynamic_cast<Distanse*>(new PinDistanse);
            }
            else {
                distanse = dynamic_cast<Distanse*>(new Distanse);
            }
            searchEngin->setDistanse(*distanse);
        }

    }
}

vector<int>* EngineFacade::find(string query) {
    if (searchEngin != nullptr) {
        return searchEngin->find(query);
    }
    else {
        throw logic_error("object not initialaze");
    }
}

EngineFacade::~EngineFacade() {
    if (searchEngin != nullptr)
    {
        delete searchEngin;
    }
}

