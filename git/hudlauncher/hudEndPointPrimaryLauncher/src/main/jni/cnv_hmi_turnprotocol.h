#ifndef _CNV_HMI_TURNPROTOCOL_H_
#define _CNV_HMI_TURNPROTOCOL_H_

#define GET_BIT_VALUE(Value,nBit) ((Value>>nBit) & 1)	//取某位Bit是否已置
#define SET_BIT_VALUE(Value,nBit) (Value = (Value | (1<<nBit)))	//置某位Bit
#define CLEAN_BIT_VALUE(Value,nBit) (Value = (Value & (~(1<<nBit))))	//清某位Bit

typedef	enum{
	GuidanceInfoType_Turn = 0,	//转向
	GuidanceInfoType_HightWay,	//高速
	GuidanceInfoType_Lane,	    //车道
	GuidanceInfoType_Camera,	//电子眼
	GuidanceInfoType_DetailJV,	//JV详细信息
	GuidanceInfoType_CatchJV,	//JV截图
	GuidanceInfoType_CatchMap,	//地图截图

	GuidanceInfoType_Max = 32,	//最大值 都要加在此之前
}GuidanceInfoType;

/*=============================================================
内存映射
================================================================*/
typedef struct tag_GuidanceInfoMemMap
{
	long lVersion;				//当前协议版本
	long lMapSize;				//映射大小
	unsigned char uMapName[32];	//映射名字
}GuidanceInfoMemMap;

typedef struct tag_GuidanceInfoDataDir
{
	long lOffset;				//与映射内存首地址的偏移
	long lMapSize;				//大小
	long lIsDataValid;			//数据是否有效 一般用于的区分从有到无数据或从无到有的情况
}GuidanceInfoDataDir;

typedef struct tag_MemMapDataHeader
{
	long lGuidanceDataFlag;							//某位置1表示有信息变化,具休哪位表示什么见GuidanceInfoType
	GuidanceInfoDataDir lGuidanceInfoDataDir[GuidanceInfoType_Max];			//数据目录(作为索引用)
}MemMapDataHeader;


typedef struct tag_GuidanceHightWayInfo
{
	unsigned short uSAName[32];	//服务区名字
	long lSADis;				//服务区距离
	unsigned short uJCName[32];	//连接口名字
	long lJCDis;				//连接口距离
	unsigned short uICName[32];	//出入口名字
	long lICDis;				//出入口距离
}GuidanceHightWayInfo;

typedef struct tag_GuidanceJVInfo
{
	long lJVDis;	//JV距离
}GuidanceJVInfo;

typedef struct tag_GuidanceLaneInfo
{
	long lNumOfLanes;	//车道个数
	char LaneStates[16];//车道信息,最高位为是否要高亮(表示推荐车道),其它表示类型
}GuidanceLaneInfo;


typedef struct tag_CatchImageInfo
{
	char strImagePixel[32];	//图片像素说明，比如800X480
	long lImageOffset;		//偏移
	long lImageSize;		//大小
}CatchImageInfo;

/*=============================================================
转向信息结构值定义
================================================================*/
typedef struct tag_GUIDANCEINFO
{
	long lDrection; //转弯方向
	long lDistance; //距离诱导点的距离，单位m(有路径)
	long lRemainDistance; //距离目的地的距离，单位m
	long lTotalDistance; //出发地与目的地之间的总距离，单位m
	long lRemainTime; //距离目的地的剩余时间
	long lTotalTime; //出发地与目的地之间的总时间
	char szCurrentRoadName[64]; //当前道路名字
	char szNextRoadName[64]; //下一道路名字
	long lCurrentRoadType; //当前道路类型
	long lCurrentSpeed; //当前车速，单位km/h
	long lCurrentLimitedSpeed;//当前限制车速，单位km/h
	long lCurrentGPSAngle; //当前GPS 角度，以正北为起始方向，顺时针旋转的角度
	long lExitIndexRoads; //环岛出口序号
	long lNumOfOutRoads; //环岛出口数
	long lReserve; //保留
}GUIDANCEINFO;


/*=============================================================
转向信息电子眼结构定义
================================================================*/
typedef struct tag_CAMERAINFO
{
	short iType; //电子眼类型
	short iSpeed; //电子眼限速值
	long  lDistance; //电子眼距离
}CAMERAINFO;

#endif
