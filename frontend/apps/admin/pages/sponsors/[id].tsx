import { useTranslations } from "use-intl";
import SponsorForm from "@/components/organisms/forms/SponsorForm";
import ResourceEditorPage from "@/components/templates/ResourceEditorPage";
import { admin } from "@/lib/api";

export default function EditSponsorPage() {
  const t = useTranslations("admin.sponsors");
  return (
    <ResourceEditorPage title={(sponsor) => t("editTitle", { name: sponsor.name })} load={admin.sponsors.get}>
      {(sponsor) => <SponsorForm sponsor={sponsor} />}
    </ResourceEditorPage>
  );
}
