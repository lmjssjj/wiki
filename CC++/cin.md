```c++
int data[5];

	for (int i = 0; i < 5;i++) {

		while (!(cin >> data[i]))
		{
			cin.clear();
			while (cin.get()!='\n')
			{
				continue;
			}
			cout << "place enter a num"<<endl;

		}

	}
```

