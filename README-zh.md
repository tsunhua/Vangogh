# Vangogh
Vangogh是一款Android平台上的简易的图片选择器，只关注图片选择的事，你可以将Data方面的工作交给Vangogh，然后专心去构造你的View。

![ScreenShot1](https://raw.githubusercontent.com/LinLshare/Vangogh/master/screenshot/zh/ss_1.png)
![ScreenShot2](https://raw.githubusercontent.com/LinLshare/Vangogh/master/screenshot/zh/ss_2.png)
![ScreenShot3](https://raw.githubusercontent.com/LinLshare/Vangogh/master/screenshot/zh/ss_3.png)
![ScreenShot4](https://raw.githubusercontent.com/LinLshare/Vangogh/master/screenshot/zh/ss_4.png)

## Vangogh不做的
1. 不做动态权限申请
2. 不做View和Activity
3. 不做Adapter
4. 也没有图片压缩

## Vangogh做的
1. 通过文件名、文件路径、文件类型和数量进行过滤
2. 选择或者不选择图片

## 使用
1. 初始化

```java
Filter filter = new Filter.Builder().mimType(MimeType.JPEG)
                                    .nameRegex(".*wx_camera.*")
                                    .limitCount(3)
                                    .pathContain("/tencent/MicroMsg/WeiXin")
                                    .build();
Vangogh.create(filter).init(this);
```

2. 从Vangogh中获取相册和图片列表 

```java
List<Album> alba = Vangogh.albumList();
List<Image> imageList = Vangogh.imageList(album);
```

3. 当用户点击图片时调用Vangogh的toggleSelect方法 

```java
Vangogh.getInstance().toggleSelect(album, image);
```

4. 当OK的时候获取用户选中的图片 

```java
Map<Album, List<Image>> albumListMap = Vangogh.selectedImageMap();
```
5. 清除所选

```java
Vangogh.selectNone();
```

## 福利
Sample包提供了额外的View实现和动态权限申请。

## 致谢
1. [darsh2/MultipleImageSelect](https://github.com/darsh2/MultipleImageSelect) 
2. [zhihu/Matisse](https://github.com/zhihu/Matisse)