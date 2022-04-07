
#include "Utils.h"

unsigned int byteToInt(char* byts, int size) {

	int max;

	if (size < 0 || size > 4)
	{
		max = 4;
	}
	else {
		max = size;
	}


	unsigned int out = 0;

	for (int i = 0; i < max; i++) {
		unsigned char byte = byts[i];
		out += byte << (8 * (max - 1 - i));
	}

	return out;
}