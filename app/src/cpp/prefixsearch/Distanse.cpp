
#include "Distanse.h"

int Distanse::getMaxDistanse(int word_lenght) {
	return 0;
}

int RusDistanse::getMaxDistanse(int word_lenght) {
	if (word_lenght >= 10) {
		return 3;
	}
	if (word_lenght >= 6){
		return 2;
	}
	if (word_lenght >= 4) {
		return 1;
	}
	return 0;
}

int PinDistanse::getMaxDistanse(int word_lenght) {
	if (word_lenght >= 8) {
		return 4;
	}
	if (word_lenght >= 6) {
		return 3;
	}
	if (word_lenght >= 6) {
		return 2;
	}
	if (word_lenght >= 4) {
		return 2;
	}
	if (word_lenght >= 3) {
		return 1;
	}
	return 0;
}