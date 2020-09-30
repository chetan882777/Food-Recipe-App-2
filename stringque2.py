def removeadjacentduplicate(data):
    lim=(len(data)//2)+1
    for _ in range(lim):
        data2=''
        last=0
        for val in data:
            if last==val:
                for val2 in data2[::-1]:
                    if val2==last:
                        data2=data2[:-1]
                continue
            else:
                data2=data2+val
                last=val
        data=data2
    return data

data='dassam'
print(removeadjacentduplicate(data))
