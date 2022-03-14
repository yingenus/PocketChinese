
#include "SearchEng.h"
#include <unordered_map>
#include <algorithm>

using namespace std;

vector<string> split(string s, string delimiter);
bool comp(Candidate *candidate1, Candidate *candidate2);
SentenseExtractor::VarianteWord* copyVariante(SentenseExtractor::VarianteWord* variant);

SearchEng::SearchEng(PrefixTree* _tree, SentenseExtractor* _extractor, Distanse* _distanse) {
	tree = _tree;
	extractor = _extractor;
    distanse = _distanse;
}

std::vector<int>* SearchEng::find(std::string query) {
	
    vector<string> words = split(query, " ");

    unordered_map<int, Candidate*> candidateMap;

    for (int index = 0; index < words.size(); index++) {
        vector<Candidate*>* newCandidates = findForWord(words[index]);

        for (int i = 0; i < newCandidates->size(); i++) {

            Candidate *candidate = (*newCandidates)[i];
            int hesh = getHash(candidate->word);

            auto item = candidateMap.find(hesh);

            if (item != candidateMap.end()) {
                Candidate* _candidate = item->second;
                _candidate->common_distanse += candidate->common_distanse;
                _candidate->involc_words += 1;
            }
            else {
                Candidate* _candidate = new Candidate;
                *_candidate = *candidate;
                _candidate->involc_words = 1;
                candidateMap.insert(pair<int, Candidate*>(hesh, _candidate));
            }
        }

        for (Candidate* ctandidate : *newCandidates) delete ctandidate;
        delete newCandidates;
    }

    vector<Candidate*> *candidates = new vector<Candidate*>;
    candidates->reserve(candidateMap.size());

    for (auto item : candidateMap) {
        candidates->push_back(item.second);
    }

    std::sort(candidates->begin(), candidates->end(), comp);

    unordered_map<int, int> idsMap;
    
    vector<int>* ids = new vector<int>;

    idsMap.reserve(candidates->size());
    ids->reserve(candidates->size());
    
    for (int i = 0; i < candidates->size(); i++) {
        int id = (*candidates)[i]->word.id;
        auto item = idsMap.find(id);

        if (item == idsMap.end())
        {
            ids->push_back(id);
            idsMap.insert(pair<int, int>(id, id));
        }

    }

    return ids;
}

void SearchEng::setDistanse(Distanse& _distanse) {
    Distanse* old = distanse;
    distanse = &_distanse;
    if (old != nullptr)
    {
        delete old;
    }
}

bool comp(Candidate *candidate1, Candidate *candidate2) {
    

    if (candidate1->involc_words < candidate2->involc_words){
        return true;
    }
    else if (candidate1->involc_words == candidate2->involc_words) {
        if (candidate1->common_distanse < candidate2->common_distanse)
        {
            return true;
        }
        else if (candidate1->common_distanse == candidate2->common_distanse) {
            if (candidate1->word.weight < candidate2->word.weight)
            {
                return true;
            }
        }
    }
 
    return false;

}

std::vector<Candidate*>* SearchEng::findForWord(string word) {

    vector<ResultWord*>* unitWords = tree->getSimular(word, distanse->getMaxDistanse(word.length()));

    vector<SentenseExtractor::ResultVarianteWords*>* variants = extractor->extract(*unitWords);

    unordered_map<unsigned int, Candidate*> candidateMap;

    for (int index = 0; index < variants-> size(); index++) {

        SentenseExtractor::ResultVarianteWords* variantsResult = (*variants)[index];
        
        vector<SentenseExtractor::VarianteWord> *words = variantsResult->varianteWords;

        for (int var_i = 0; var_i < words->size(); var_i++) {
            
            SentenseExtractor::VarianteWord word = (*words)[var_i];
            unsigned int hesh = getHash(word);

            auto item = candidateMap.find(hesh);

            if (item != candidateMap.end()) {
                if (item ->second->common_distanse < variantsResult->word_result->distanse)
                {
                    Candidate *candidate = item->second;
                    candidate -> common_distanse = variantsResult -> word_result->distanse;
                }
            }
            else{
                Candidate *candidate = new Candidate;

                candidate->common_distanse = variantsResult->word_result->distanse;
                candidate->word = word;
                candidateMap.insert(pair<unsigned int,Candidate*>(hesh, candidate));
            }
        }
    }

    for (ResultWord* word : *unitWords) delete word;

    delete  unitWords;

    for (SentenseExtractor::ResultVarianteWords* variant : *variants) {

        delete variant->varianteWords;
        //delete variant->word_result;
        delete variant;
    }

    delete  variants;

    vector<Candidate*>* candidats = new vector<Candidate*>;

    candidats->reserve(candidateMap.size());

    for (auto item : candidateMap) {
        candidats->push_back(item.second);
    }

    return candidats;
}

unsigned int SearchEng::getHash(SentenseExtractor::VarianteWord &word) {
    return ( word.id << 10) + word.index;
}

vector<string> split(string s, string delimiter) {
    size_t pos_start = 0, pos_end, delim_len = delimiter.length();
    string token;
    vector<string> res;

    while ((pos_end = s.find(delimiter, pos_start)) != string::npos) {
        token = s.substr(pos_start, pos_end - pos_start);
        pos_start = pos_end + delim_len;
        res.push_back(token);
    }

    res.push_back(s.substr(pos_start));
    return res;
}

SentenseExtractor::VarianteWord* copyVariante(SentenseExtractor::VarianteWord* variant) {
    SentenseExtractor::VarianteWord* _new = new SentenseExtractor::VarianteWord;
    *_new = *variant;
    return _new;
}

SearchEng::~SearchEng() {
	delete tree;
	delete extractor;
    delete distanse;
}