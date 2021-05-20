import BannerText from "../../layout/BannerText";
import HorizontalCenter from "../../layout/HorizontalCenter";
import DecisionButton from "../DecisionButton";

export default function PriorityChoice() {
    return <div>
        <BannerText as="p">Cast spells, activate abilities, or play a land.</BannerText>
        <HorizontalCenter>
            <DecisionButton optionToChoose="Pass">Pass</DecisionButton>
        </HorizontalCenter>
    </div>;
}
