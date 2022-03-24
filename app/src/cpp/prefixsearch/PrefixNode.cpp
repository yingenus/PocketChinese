

#include "PrefixNode.h"

using namespace std;

Node::Node() {

}

void Node::add(string &key, Word &word) {

	if (key.empty()) {
		leaf = &word;
		return;
	}
	
	char first_char = key[0];
	
	string sub_key = getSubKey(key);
	
	if (isContainNode(first_char)) {
		findNode(first_char)->add(sub_key, word);
	}
	else{

		int new_size = array_lenght + 1;

		Link **new_links = new Link*[new_size];

		bool was_added = false;

		if (array_lenght == 0) {
			Node* node = new Node();
			node->add(sub_key, word);

			Link* link = new Link();
			link->key = first_char;
			link->node = node;

			new_links[0] = link;
		}
		else{
			for (int i = 0; i < new_size; i++) {

				if (i == new_size-1)
				{
					if (!was_added) {
						Node* node = new Node();
						node->add(sub_key, word);

						Link* link = new Link();
						link->key = first_char;
						link->node = node;


						new_links[i] = link;
						was_added = true;
					}
				}
				else if (!was_added) {

					Link* candidate = links[i];

					if (candidate->key > first_char) {
						Node* node = new Node();
						node->add(sub_key, word);

						Link* link = new Link();
						link->key = first_char;
						link->node = node;


						new_links[i] = link;
						new_links[i + 1] = candidate;
						was_added = true;
					}
					else {
						new_links[i] = candidate;
					}

				}
				else {
					new_links[i + 1] = links[i];
				}

			}
		}
		if(links != nullptr){
			delete[] links;
		}
		links = new_links;
		array_lenght = new_size;
	}
	
}

bool Node::isContainNode(char key) {
	for (int i = 0; i < array_lenght; i++) {
		if (links[i]->key == key) {
			return true;
		}
	}
	return false;
}

string Node::getSubKey(string& key) {

	int lenght = key.length();

	if (lenght == 0) {
		return string();
	}
	else if(lenght == 1){
		
		return (string());
	}
	else {
		return (key.substr(1, lenght - 1));
	}

}

vector<ResultWord*>* Node::get(string& key) {

	
	vector<ResultWord*> *resultVector = new vector<ResultWord*>;
	
	if (!key.empty()) { // ���� � ����� ��� �������� ����� ������ ��� ����� ����� ��� ����� � ������ ����� � ������� � ���

		char first_key = key[0];

		Node* node = findNode(first_key);

		if ( node != nullptr){
			 
			string sub_key = getSubKey(key);

			vector<ResultWord*>*  sub_array = node->get(sub_key);

			if (sub_array->size() > 0) {

				int sub_size = sub_array->size();

				for (int i = 0; i < sub_size; i++) {
					ResultWord* resultWord = (*sub_array)[i];
					string prefix_key = resultWord->key_word;
					resultWord->key_word = first_key + prefix_key;
					resultVector->push_back(resultWord);
				}
			}

			delete sub_array;
		}

		
	}
	else if( array_lenght > 0){ //���� � ���� ���� ������ �� ������ ����� ������� ��� � �������� ����������

		if (leaf != nullptr) { // ��� ��� ��� ��� ������� ������������������ ������ ��� �������� ��� ����� � ������ ������� ����� ����������� ����

			ResultWord *leafWord = new ResultWord;
			leafWord->key_word = "";
			leafWord->word = leaf;
			leafWord->distanse = 0;
			resultVector->push_back(leafWord);
		}

		for (int key_ind = 0; key_ind < array_lenght; key_ind++) { // ����� ������� ��� ������ ���� 

			vector<ResultWord*>* sub_array;

			string empty = "";

			Link* link = links[key_ind];

			sub_array = link->node->get(empty);

			if (sub_array->size() > 0) {

				int sub_size = sub_array->size();

				for (int i = 0; i < sub_size; i++) {
					ResultWord *resultWord = (*sub_array)[i];
					string prefix_key = resultWord->key_word;
					resultWord->key_word = link->key + prefix_key;
					resultVector->push_back(resultWord);
				}
			}

			delete sub_array;
		}
	}
	else if(leaf != nullptr)// ��� ��� ��� ��� ������� ������������������ ������ ��� �������� ��� ����� � ������ ������� ����� ����������� ����
	{
		ResultWord *leafWord = new ResultWord;
		leafWord->key_word = "";
		leafWord->word = leaf;
		leafWord->distanse = 0;
		resultVector->push_back(leafWord);
	}

	return resultVector;
}

/*
vector<ResultWord*>* Node::getSimilar(string& key, int distanse, bool was_insert, bool was_skip) {

	if (key.empty() || distanse == 0) { // ���� ������ ������  ������� ����� ������ �������� ������ getSimilar ������������ get
		return get(key);
	}
	else {
		vector<ResultWord*>* resultVector = new vector<ResultWord*>;

		// ��������� ������������������ ������� ���������� �������

		char first_key = key[0]; // ����� �� ������� ���� ���� �����
		string suffix = getSubKey(key);// ���������� �������

		Node* exactlyNode = findNode(first_key); // ����������� ������ ����� ����� ������� ������
		if (exactlyNode != nullptr) {

			vector<ResultWord*>* exactlyVector;

			exactlyVector = exactlyNode->getSimilar(suffix, distanse, false, false);

			if (!exactlyVector->empty()) {
				//resultVector->resize(resultVector->size() + exactlyVector->size());

				int result_size = exactlyVector->size();
				for (int i = 0; i < result_size; i++) {
					ResultWord* word = exactlyVector->at(i);

					word->key_word = first_key + word->key_word;

					resultVector->push_back(word);
				}

			}
			delete exactlyVector;
		}

		
		if (array_lenght > 0) {


			// ���������� ����� �������� ������
			vector<ResultWord*>* replaseVector = new vector<ResultWord*>;

			for (int link_index = 0; link_index < array_lenght; link_index++) {

				vector<ResultWord*>* resevedVector;

				resevedVector = links[link_index]->node->getSimilar(suffix, distanse - 1, false, false);


				if (!resevedVector->empty()) {
					//replaseVector->resize(replaseVector->size() + resevedVector->size());

					int result_size = resevedVector->size();
					for (int i = 0; i < result_size; i++) {
						ResultWord* word = resevedVector->at(i);

						word->key_word = links[link_index]->key + word->key_word;

						replaseVector->push_back(word);
					}
				}

				delete resevedVector;
			}

			if (!replaseVector->empty()) {
				//resultVector->resize(resultVector->size() + replaseVector->size());

				int result_size = replaseVector->size();
				for (int i = 0; i < result_size; i++) {
					ResultWord* word = replaseVector->at(i);
					resultVector->push_back(word);
				}
			}

			delete replaseVector;


			if (!was_insert) {
				// ��������� �������� ��������
				vector<ResultWord*>* skipVector = new vector<ResultWord*>;

				for (int link_index = 0; link_index < array_lenght; link_index++) {

					vector<ResultWord*>* resevedVector;

					resevedVector = links[link_index]->node->getSimilar(key, distanse - 1, false, true);


					if (!resevedVector->empty()) {
						//skipVector->resize(skipVector->size() + resevedVector->size());

						int result_size = resevedVector->size();
						for (int i = 0; i < result_size; i++) {
							ResultWord* word = resevedVector->at(i);

							word->key_word = links[link_index]->key + word->key_word;

							skipVector->push_back(word);
						}
					}

					delete resevedVector;
				}

				if (!skipVector->empty()) {
					//resultVector->resize(resultVector->size() + skipVector->size());

					int result_size = skipVector->size();
					for (int i = 0; i < result_size; i++) {
						ResultWord* word = skipVector->at(i);
						resultVector->push_back(word);
					}
				}

				delete skipVector;
			}

			if (!was_skip) {
				// ��������� �������� �������
				vector<ResultWord*>* insertVector = new vector<ResultWord*>;

				for (int link_index = 0; link_index < array_lenght; link_index++) {

					vector<ResultWord*>* resevedVector;

					resevedVector = getSimilar(suffix, distanse - 1, true, false);


					if (!resevedVector->empty()) {
						//insertVector->resize(insertVector->size() + resevedVector->size());

						int result_size = resevedVector->size();
						for (int i = 0; i < result_size; i++) {
							ResultWord* word = resevedVector->at(i);
							insertVector->push_back(word);
						}
					}

					delete resevedVector;
				}

				if (!insertVector->empty()) {
					//resultVector->resize(resultVector->size() + insertVector->size());

					int result_size = insertVector->size();
					for (int i = 0; i < result_size; i++) {
						ResultWord* word = insertVector->at(i);
						resultVector->push_back(word);
					}
				}

				delete insertVector;
			}

			

		}

		return resultVector;
	}
}
*/

vector<ResultWord*>* Node::getSimilar(string& key, int distanse, bool was_insert, bool was_skip) {

	if (key.empty() || distanse == 0) { // ���� ������ ������  ������� ����� ������ �������� ������ getSimilar ������������ get
		return get(key);
	}
	else {
		vector<ResultWord*>* resultVector = new vector<ResultWord*>;

		// ��������� ������������������ ������� ���������� �������

		char first_key = key[0]; // ����� �� ������� ���� ���� �����
		string suffix = getSubKey(key);// ���������� �������

		Node* exactlyNode = findNode(first_key); // ����������� ������ ����� ����� ������� ������
		if (exactlyNode != nullptr) {

			vector<ResultWord*>* exactlyVector;

			exactlyVector = exactlyNode->getSimilar(suffix, distanse, false, false);

			if (!exactlyVector->empty()) {
				resultVector->reserve(resultVector->size() + exactlyVector->size());

				int result_size = exactlyVector->size();
				for (int i = 0; i < result_size; i++) {
					ResultWord *word = (*exactlyVector)[i];

					word->key_word = first_key + word->key_word;

					resultVector->push_back(word);
				}

			}
			delete exactlyVector;
		}


		if (array_lenght > 0) {


			// ���������� ����� �������� �����
			vector<ResultWord*>* replaseVector = new vector<ResultWord*>;

			for (int link_index = 0; link_index < array_lenght; link_index++) {

				vector<ResultWord*>* resevedVector;

				if (links[link_index]->key == first_key)
				{
					continue;
				}

				resevedVector = links[link_index]->node->getSimilar(suffix, distanse - 1, false, false);


				if (!resevedVector->empty()) {
					replaseVector->reserve(replaseVector->size() + resevedVector->size());

					int result_size = resevedVector->size();
					for (int i = 0; i < result_size; i++) {
						ResultWord *word = (*resevedVector)[i];

						word->key_word = links[link_index]->key + word->key_word;
						word->distanse += 1;

						replaseVector->push_back(word);
					}
				}

				delete resevedVector;
			}

			if (!replaseVector->empty()) {
				resultVector->reserve(resultVector->size() + replaseVector->size());

				int result_size = replaseVector->size();
				for (int i = 0; i < result_size; i++) {
					ResultWord *word = (*replaseVector)[i];
					resultVector->push_back(word);
				}
			}

			delete replaseVector;

			if (key.size() > 0) {

				if (!was_insert) {
					// ��������� �������� ��������
					vector<ResultWord*>* skipVector = new vector<ResultWord*>;

					for (int link_index = 0; link_index < array_lenght; link_index++) {

						vector<ResultWord*>* resevedVector;

						resevedVector = links[link_index]->node->getSimilar(key, distanse - 1, false, true);


						if (!resevedVector->empty()) {
							skipVector->reserve(skipVector->size() + resevedVector->size());

							int result_size = resevedVector->size();
							for (int i = 0; i < result_size; i++) {
								ResultWord *word = (*resevedVector)[i];

								word->key_word = links[link_index]->key + word->key_word;
								word->distanse += 1;

								skipVector->push_back(word);
							}
						}

						delete resevedVector;
					}

					if (!skipVector->empty()) {
						resultVector->reserve(resultVector->size() + skipVector->size());

						int result_size = skipVector->size();
						for (int i = 0; i < result_size; i++) {
							ResultWord *word = (*skipVector)[i];
							resultVector->push_back(word);
						}
					}

					delete skipVector;
				}

				if (!was_skip) {
					// ��������� �������� �������
					vector<ResultWord*>* insertVector = new vector<ResultWord*>;

					

					vector<ResultWord*>* resevedVector;

					resevedVector = getSimilar(suffix, distanse - 1, true, false);

					if (!resevedVector->empty()) {
						resultVector->reserve(resultVector->size() + resevedVector->size());

						int result_size = resevedVector->size();
						for (int i = 0; i < result_size; i++) {
							ResultWord* word = (*resevedVector)[i];
							word->distanse += 1;
							resultVector->push_back(word);
						}
					}
					delete resevedVector;

					/*
					if (!resevedVector->empty()) {
							insertVector->reserve(insertVector->size() + resevedVector->size());

						int result_size = resevedVector->size();
						for (int i = 0; i < result_size; i++) {
							ResultWord *word = (*resevedVector)[i];
							word->distanse += 1;
							insertVector->push_back(word);
						}
					}

					delete resevedVector;
					

					if (!insertVector->empty()) {
						resultVector->reserve(resultVector->size() + insertVector->size());

						int result_size = insertVector->size();
						for (int i = 0; i < result_size; i++) {
							ResultWord *word = (*insertVector)[i];
							resultVector->push_back(word);
						}
					}

					delete insertVector;
					*/
				}

			}
		}

		return resultVector;
	}
}

Node* Node::findNode(char key) {
	int l = 0, r = array_lenght - 1;

	if ( r == -1)
	{
		return nullptr;
	}

	while (r > l) {
		int m = (l + r) / 2;    //������������� �������!

		Link* link = links[m];

		if (link->key < key) {
			l = m + 1;
		}
		else if (link->key > key) {
			r = m - 1;
		}
		else {
			return link->node;
		}
	}

	Link* link = links[l];

	if (link->key == key) {
		return link->node;
	}
	else {
		return nullptr;
	}
}

int Node::my_size() {
	int self_size = sizeof(this);

	int arry_size = 0;

	int all_links_size = 0;

	if (links != nullptr) {
		for (int i = 0; i < array_lenght; i++) {
			Link* node = links[i];

			all_links_size += sizeof(node->node);
			all_links_size += sizeof(node);
		}
		arry_size = sizeof(*links);
	}

	int leaf_size = sizeof(*leaf);

	return self_size + arry_size + all_links_size + leaf_size;
}

Node::~Node() {

	delete leaf;

	if (links != nullptr) {
		for (int i = 0; i < array_lenght; i++) {
			Link* node = links[i];

			delete node->node;
			delete node;
		}
		delete[] links;
	}
}



