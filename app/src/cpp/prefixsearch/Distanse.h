#pragma once

class Distanse{
public:
	virtual int getMaxDistanse(int wordLenght);
};

class RusDistanse : public Distanse {
	int getMaxDistanse(int wordLenght);
};

class PinDistanse : public Distanse {
	int getMaxDistanse(int wordLenght);
};
