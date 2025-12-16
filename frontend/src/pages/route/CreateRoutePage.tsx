import { useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useCreateRoute } from '../../hooks/useRoute';

export function CreateRoutePage() {
  const { teamSlug } = useParams<{ teamSlug: string }>();
  const { t } = useTranslation('routes');
  const { t: tCommon } = useTranslation('common');
  const { t: tErrors } = useTranslation('errors');

  const createRoute = useCreateRoute(teamSlug!);

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [difficulty, setDifficulty] = useState<'EASY' | 'MODERATE' | 'HARD' | 'EXPERT'>('MODERATE');
  const [surfaceType, setSurfaceType] = useState<'ROAD' | 'GRAVEL' | 'MTB' | 'MIXED'>('ROAD');
  const [isPublic, setIsPublic] = useState(false);
  const [gpxFile, setGpxFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Validate file type
      if (!file.name.endsWith('.gpx')) {
        setError(t('create.validation.invalidFileType'));
        setGpxFile(null);
        return;
      }
      // Validate file size (10MB max)
      if (file.size > 10 * 1024 * 1024) {
        setError(t('create.validation.fileTooLarge'));
        setGpxFile(null);
        return;
      }
      setError(null);
      setGpxFile(file);
    }
  }, [t]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!gpxFile) {
      setError(t('create.validation.fileRequired'));
      return;
    }

    try {
      await createRoute.mutateAsync({
        name,
        description: description || undefined,
        difficulty,
        surfaceType,
        isPublic,
        gpxFile,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : tErrors('api.unknown'));
    }
  };

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/routes`}
          className="text-indigo-600 hover:text-indigo-900 text-sm font-medium"
        >
          ← {t('create.backToList')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-2 text-gray-600">{t('create.subtitle')}</p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* GPX File Upload */}
        <div>
          <label htmlFor="gpxFile" className="block text-sm font-medium text-gray-700">
            {t('create.form.gpxFile')} *
          </label>
          <div className="mt-1">
            <input
              id="gpxFile"
              name="gpxFile"
              type="file"
              accept=".gpx"
              onChange={handleFileChange}
              className="block w-full text-sm text-gray-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-md file:border-0
                file:text-sm file:font-semibold
                file:bg-indigo-50 file:text-indigo-700
                hover:file:bg-indigo-100"
              required
            />
          </div>
          <p className="mt-2 text-sm text-gray-500">
            {t('create.form.gpxFileHint')}
          </p>
        </div>

        {/* Name */}
        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">
            {t('create.form.name')} *
          </label>
          <input
            id="name"
            name="name"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
            required
            maxLength={255}
          />
        </div>

        {/* Description */}
        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">
            {t('create.form.description')}
          </label>
          <textarea
            id="description"
            name="description"
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          />
        </div>

        {/* Difficulty */}
        <div>
          <label htmlFor="difficulty" className="block text-sm font-medium text-gray-700">
            {t('create.form.difficulty')}
          </label>
          <select
            id="difficulty"
            name="difficulty"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value as typeof difficulty)}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          >
            <option value="EASY">{t('difficulty.EASY')}</option>
            <option value="MODERATE">{t('difficulty.MODERATE')}</option>
            <option value="HARD">{t('difficulty.HARD')}</option>
            <option value="EXPERT">{t('difficulty.EXPERT')}</option>
          </select>
        </div>

        {/* Surface Type */}
        <div>
          <label htmlFor="surfaceType" className="block text-sm font-medium text-gray-700">
            {t('create.form.surfaceType')}
          </label>
          <select
            id="surfaceType"
            name="surfaceType"
            value={surfaceType}
            onChange={(e) => setSurfaceType(e.target.value as typeof surfaceType)}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          >
            <option value="ROAD">{t('surfaceType.ROAD')}</option>
            <option value="GRAVEL">{t('surfaceType.GRAVEL')}</option>
            <option value="MTB">{t('surfaceType.MTB')}</option>
            <option value="MIXED">{t('surfaceType.MIXED')}</option>
          </select>
        </div>

        {/* Is Public */}
        <div className="flex items-center">
          <input
            id="isPublic"
            name="isPublic"
            type="checkbox"
            checked={isPublic}
            onChange={(e) => setIsPublic(e.target.checked)}
            className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
          />
          <label htmlFor="isPublic" className="ml-2 block text-sm text-gray-900">
            {t('create.form.isPublic')}
          </label>
        </div>

        {/* Submit Buttons */}
        <div className="flex justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}/routes`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={createRoute.isPending || !gpxFile}
            className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {createRoute.isPending ? tCommon('status.creating') : t('create.submit')}
          </button>
        </div>
      </form>
    </div>
  );
}
