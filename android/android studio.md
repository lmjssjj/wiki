# 添加自定义framework.jar

## android studio4.2之前版本

```
build.gradle(model)
dependencies {
    compileOnly files('libs/framework.jar')
}
build.gradle(项目根目录)
allprojects {
    gradle.projectsEvaluated {
        tasks.withType(JavaCompile) {
            options.compilerArgs.add('-Xbootclasspath/p:app\\libs\\framework.jar')//相对路径
        }
    }
}
```

## android studio4.2之后版本

```
build.gradle(model)
dependencies {
    compileOnly files('libs/framework.jar')
}
build.gradle(项目根目录)
allprojects {
    repositories {
        google()
        jcenter()
    }
    gradle.projectsEvaluated {
//        tasks.withType(JavaCompile) {
//            options.compilerArgs.add('-Xbootclasspath/p:app\\libs\\framework.jar')
//        }
        tasks.withType(JavaCompile) {
            Set<File> fileSet = options.bootstrapClasspath.getFiles()
            List<File> newFileList =  new ArrayList<>();
            //相对位置，根据存放的位置修改路径
            newFileList.add(new File("./app/libs/framework.jar"))
            newFileList.addAll(fileSet)
            options.bootstrapClasspath = files(
                    newFileList.toArray()
            )
        }
    }
}
```

