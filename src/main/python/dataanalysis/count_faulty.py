import os

dir_path = "data/Arff_XV"
files = os.listdir(dir_path)

for file in files:
    with open(os.path.join(dir_path, file), "r") as dataset:
        is_data = False
        count_data_points = 0
        count_defective = 0
        for line in dataset.readlines():
            if is_data:
                count_data_points += 1
                #activemq-web/src/main/java/org/apache/activemq/web/SessionPool.java,1,6,101,3,0,1,0,14,2,7,0,0,2,8,21,1,8,14,19,1,3,3,6,30,14,10,19,65,26,0.32,15,45,3,27,0,8,2,70,1,0,0,1,2,1.625,1,6,2.375,1,3,1.75,1,1,0.7,0,1,1,1,101,0,1,1,0,1,1,0,FALSE,0,FALSE,0
                label = line.split(",")[-2]
                if label == "TRUE":
                    count_defective += 1
            elif line.startswith("@data"):
                is_data = True
        print(f"Program {file}: {count_defective} out of {count_data_points} ({round(count_defective / count_data_points * 100, 2)}%)")
