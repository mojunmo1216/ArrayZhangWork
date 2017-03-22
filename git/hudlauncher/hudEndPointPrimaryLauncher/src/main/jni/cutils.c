#include "cutils.h"

int getUnicodeStringLength(unsigned short* s)
{
	int length = 0;

	while (*s++ != 0) {
		length++;
	}

	return length;
}
