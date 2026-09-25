import { useState, type ChangeEvent } from "react";
import { ReactSortable } from "react-sortablejs";
import { useTranslations } from "use-intl";
import type { Message } from "@festival/shared/api/types";
import { admin } from "@/lib/api";
import { useDialogs } from "@/lib/dialogs";
import ErrorMessage from "../atoms/ErrorMessage";
import Spinner from "../atoms/Spinner";
import { Icon } from "../atoms/icons";

interface ImagesFieldProps {
  images: readonly string[];
  onChange: (images: string[]) => void;
  error: Message | undefined;
}

export default function ImagesField({ images, onChange, error }: Readonly<ImagesFieldProps>) {
  const t = useTranslations("admin.images");
  const [uploading, setUploading] = useState(false);
  const { notifyError } = useDialogs();
  const items = images.map((url) => ({ id: url }));

  async function upload(event: ChangeEvent<HTMLInputElement>) {
    const input = event.currentTarget;
    const files = [...(input.files ?? [])];
    if (files.length === 0) {
      return;
    }
    setUploading(true);
    try {
      const { urls } = await admin.media.upload(files);
      onChange([...images, ...urls]);
    } catch (uploadError) {
      notifyError(uploadError);
    } finally {
      setUploading(false);
      input.value = "";
    }
  }

  return (
    <div className="mb-2">
      <span className="block text-sm font-medium text-neutral-800">{t("label")}</span>
      <div className="flex flex-wrap gap-2">
        <ReactSortable
          list={items}
          setList={(sorted) => onChange(sorted.map((item) => item.id))}
          className="flex flex-wrap gap-2"
        >
          {images.map((url) => (
            <div key={url} className="group relative shadow-md rounded-md h-32 bg-neutral-100">
              {/* eslint-disable-next-line @next/next/no-img-element -- podgląd zdjęcia z magazynu */}
              <img src={url} alt="" className="rounded-md object-cover h-full w-full" />
              <button
                type="button"
                onClick={() => onChange(images.filter((image) => image !== url))}
                className="absolute inset-0 hidden group-hover:flex items-center justify-center rounded-md bg-black/40 text-white uppercase"
              >
                <b>{t("remove")}</b>
              </button>
            </div>
          ))}
        </ReactSortable>
        {uploading && (
          <div className="h-32 p-1 flex items-center">
            <Spinner />
          </div>
        )}
        <label className="shadow-md w-32 h-32 cursor-pointer text-center flex flex-col items-center justify-center rounded-md bg-neutral-100 border-2 border-neutral-300 hover:bg-neutral-300">
          <Icon name="upload" />
          <div>{t("upload")}</div>
          <input
            type="file"
            accept="image/jpeg,image/png,image/webp,image/gif"
            multiple
            onChange={(event) => void upload(event)}
            className="hidden"
          />
        </label>
      </div>
      <ErrorMessage message={error} />
    </div>
  );
}
