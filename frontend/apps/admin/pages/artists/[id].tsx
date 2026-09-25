import { useTranslations } from "use-intl";
import ArtistForm from "@/components/organisms/forms/ArtistForm";
import ResourceEditorPage from "@/components/templates/ResourceEditorPage";
import { admin } from "@/lib/api";

export default function EditArtistPage() {
  const t = useTranslations("admin.artists");
  return (
    <ResourceEditorPage title={(artist) => t("editTitle", { name: artist.name })} load={admin.artists.get}>
      {(artist) => <ArtistForm artist={artist} />}
    </ResourceEditorPage>
  );
}
