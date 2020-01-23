package com.volmit.ed4j.util;

public class LandingPads
{
	public static String getPadDistance(int pad)
	{
		int c = 0;
		int m = 1;
		int f = 2;
		int k = 3;

		if(pad == 1)
		{
			k = m;
		}
		if(pad == 2)
		{
			k = c;
		}
		if(pad == 3)
		{
			k = m;
		}
		if(pad == 4)
		{
			k = f;
		}
		if(pad == 5)
		{
			k = c;
		}
		if(pad == 6)
		{
			k = c;
		}
		if(pad == 7)
		{
			k = m;
		}
		if(pad == 8)
		{
			k = f;
		}
		if(pad == 9)
		{
			k = c;
		}
		if(pad == 10)
		{
			k = f;
		}
		if(pad == 11)
		{
			k = c;
		}
		if(pad == 12)
		{
			k = c;
		}
		if(pad == 13)
		{
			k = c;
		}
		if(pad == 14)
		{
			k = f;
		}
		if(pad == 15)
		{
			k = f;
		}
		if(pad == 16)
		{
			k = c;
		}
		if(pad == 17)
		{
			k = c;
		}
		if(pad == 18)
		{
			k = m;
		}
		if(pad == 19)
		{
			k = f;
		}
		if(pad == 20)
		{
			k = c;
		}
		if(pad == 21)
		{
			k = c;
		}
		if(pad == 22)
		{
			k = m;
		}
		if(pad == 23)
		{
			k = f;
		}
		if(pad == 24)
		{
			k = c;
		}
		if(pad == 25)
		{
			k = f;
		}
		if(pad == 26)
		{
			k = c;
		}
		if(pad == 27)
		{
			k = c;
		}
		if(pad == 28)
		{
			k = m;
		}
		if(pad == 29)
		{
			k = m;
		}
		if(pad == 30)
		{
			k = f;
		}
		if(pad == 31)
		{
			k = f;
		}
		if(pad == 32)
		{
			k = m;
		}
		if(pad == 33)
		{
			k = m;
		}
		if(pad == 34)
		{
			k = c;
		}
		if(pad == 35)
		{
			k = c;
		}
		if(pad == 36)
		{
			k = m;
		}
		if(pad == 37)
		{
			k = m;
		}
		if(pad == 38)
		{
			k = f;
		}
		if(pad == 39)
		{
			k = c;
		}
		if(pad == 40)
		{
			k = f;
		}
		if(pad == 41)
		{
			k = c;
		}
		if(pad == 42)
		{
			k = c;
		}
		if(pad == 43)
		{
			k = m;
		}
		if(pad == 44)
		{
			k = m;
		}
		if(pad == 45)
		{
			k = f;
		}
		if(k == 0)
		{
			return "front";
		}
		if(k == 1)
		{
			return "middle";
		}
		if(k == 2)
		{
			return "back";
		}

		return "";
	}
}
