import Typography from "@component/Typography";
import Tag from "@container/project/component/Tag";
import bookmark from "@assets/bookmark.svg";

const BoardList = () => {
    return (
        <div className="my-10 flex w-full justify-between rounded-lg border-2 border-solid border-borderline p-20 shadow-lg">
            <div>
                <div className="flex w-48 items-center justify-center rounded bg-deadline ">
                    <Typography type="SmallLabel" text="모집중" styles="text-white" />
                </div>
                <h1 className="my-4 cursor-pointer text-24 font-bold">여기가 프로젝트 제목입니다~!</h1>
                <div className="flex">
                    <Tag type="PROJECT" text="Java"></Tag>
                    <Tag type="PROJECT" text="JavaScript"></Tag>
                </div>
                <div className="mt-4 text-14 text-gray-600">2023-08-24 ~ 2023-09-22</div>
            </div>
            <img src={bookmark} className="h-28 w-28 cursor-pointer" />
        </div>
    );
};

export default BoardList;
