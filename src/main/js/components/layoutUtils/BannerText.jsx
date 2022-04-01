import {createElement} from "preact";
import {addClass} from "../../utils/element-utils";

export default function BannerText({as, className, ...props}) {
    as = as || "h1";
    return createElement(as, {className: addClass("text-center mt-3", className), ...props});
}
