import Typography from "@component/Typography";
import { Switch } from "@material-tailwind/react";

function Toggle() {
    return (
        <>
            <div className="flex items-center justify-center gap-8">
                <Typography type="Highlight" text="모집 중"></Typography>
                <Switch
                    defaultChecked={true}
                    id="custom-switch-component"
                    ripple={false}
                    className="h-full w-full checked:bg-main"
                    containerProps={{
                        className: "w-40 h-20",
                    }}
                    circleProps={{
                        className: "before:hidden w-18 h-18 left-0.5 border-none",
                    }}
                    crossOrigin={undefined}
                />
            </div>
        </>
    );
}
export default Toggle;
