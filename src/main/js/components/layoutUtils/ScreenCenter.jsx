import HorizontalCenter from "./HorizontalCenter";
import VerticalCenter from "./VerticalCenter";

export default function ScreenCenter({children}) {
    return <VerticalCenter>
        <HorizontalCenter>
            {children}
        </HorizontalCenter>
    </VerticalCenter>
}
