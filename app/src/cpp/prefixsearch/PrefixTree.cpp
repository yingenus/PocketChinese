

#include "PrefixTree.h"
#include <unordered_map>
#include <chrono>

using namespace std;

PrefixTree::PrefixTree() {
	root = new Node();
}


void PrefixTree::add( string& key, Word& word) {
	root->add(key, word);
}

vector<ResultWord*>* PrefixTree::get(std::string& key) {
	return root->get(key);
}

vector<ResultWord*>* PrefixTree::getSimular(std::string& key, int distanse) {

	auto begin = std::chrono::steady_clock::now();

	vector<ResultWord*>* unUnike = root->getSimilar(key, distanse, false, false);

	auto end1 = std::chrono::steady_clock::now();

	vector<ResultWord*>* unike = makeUnike(unUnike);

	vector<ResultWord*>* copyed = new vector<ResultWord*>;
	copyed->resize(unike->size());
	
	for (int i = 0; i < unike->size(); i++) {
		ResultWord* _new = new ResultWord;

		*_new = *(*unike)[i];

		(*copyed)[i] = _new;
		//copyed->push_back(_new);
	}
	
	for (ResultWord* word : *unUnike) {
		delete word;
	}

	delete unUnike;
	delete unike;

	auto end2 = std::chrono::steady_clock::now();

	auto elapsed1_ms = std::chrono::duration_cast<std::chrono::milliseconds>(end1 - begin);
	auto elapsed2_ms = std::chrono::duration_cast<std::chrono::milliseconds>(end2 - end1);

	return copyed;
}

vector<ResultWord*>* PrefixTree::makeUnike(vector<ResultWord*> *ununikeVector) {

	unordered_map< int, ResultWord*> unikeMup;
	
	int vectorSize = ununikeVector->size();

	for (int index = 0; index < vectorSize; index++) {

		ResultWord *un_unike_word = (*ununikeVector)[index];
		
		auto item = unikeMup.find(un_unike_word->word->startIndex);

		if (item != unikeMup.end()) {
			if ( item->second->distanse > un_unike_word->distanse)
			{
				unikeMup.insert( pair<int,ResultWord*>(un_unike_word->word->startIndex, un_unike_word));
			}
		}
		else{
			unikeMup.insert(pair<int, ResultWord*>(un_unike_word->word->startIndex, un_unike_word));
		}
		
	}

	vector<ResultWord*> *unikeVector = new  vector<ResultWord*>;

	//unikeVector->reserve(unikeMup->size());

	for (auto item : unikeMup) {
		unikeVector->push_back(item.second);
	}

	return unikeVector;
}

int PrefixTree::mu_size() {
	int self_size = sizeof(this);
	int tree_size = root->my_size();

	return self_size + tree_size;
}

PrefixTree::~PrefixTree() {
	delete root;
}