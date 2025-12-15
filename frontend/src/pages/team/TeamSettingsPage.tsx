import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useTeam, useUpdateTeam, useDeleteTeam } from '../../hooks/useTeam';
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ApiClientError } from '../../api/client';

export function TeamSettingsPage() {
  const { t } = useTranslation('teams');
  const { t: tCommon } = useTranslation('common');
  const { teamSlug } = useParams<{ teamSlug: string }>();

  const { data: team, isLoading, error } = useTeam(teamSlug);
  const updateMutation = useUpdateTeam(teamSlug || '');
  const deleteMutation = useDeleteTeam(teamSlug || '');

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [logoUrl, setLogoUrl] = useState('');
  const [coverImageUrl, setCoverImageUrl] = useState('');
  const [maxMembers, setMaxMembers] = useState<number | ''>('');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  useEffect(() => {
    if (team) {
      setName(team.name);
      setDescription(team.description || '');
      setIsPublic(team.isPublic);
      setLogoUrl(team.logoUrl || '');
      setCoverImageUrl(team.coverImageUrl || '');
      setMaxMembers(team.maxMembers || '');
    }
  }, [team]);

  if (isLoading) {
    return <LoadingPage message={t('settings.loading')} />;
  }

  if (error || !team) {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('settings.error.title')}</h1>
          <p className="text-gray-600 mb-6">
            {t('settings.error.loadFailed')}
          </p>
          <Link
            to="/teams"
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('detail.notFound.backToTeams')}
          </Link>
        </div>
      </div>
    );
  }

  if (team.userRole !== 'ADMIN') {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('settings.error.accessDenied')}</h1>
          <p className="text-gray-600 mb-6">
            {t('settings.error.adminRequired')}
          </p>
          <Link
            to={`/teams/${teamSlug}`}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('settings.backToTeam', { teamName: team.name })}
          </Link>
        </div>
      </div>
    );
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    updateMutation.mutate({
      name: name !== team.name ? name : undefined,
      description: description !== team.description ? description : undefined,
      isPublic: isPublic !== team.isPublic ? isPublic : undefined,
      logoUrl: logoUrl !== team.logoUrl ? logoUrl || undefined : undefined,
      coverImageUrl: coverImageUrl !== team.coverImageUrl ? coverImageUrl || undefined : undefined,
      maxMembers: maxMembers !== team.maxMembers ? (maxMembers ? Number(maxMembers) : undefined) : undefined,
    });
  };

  const handleDelete = () => {
    deleteMutation.mutate();
  };

  const getFieldError = (field: string) => {
    if (updateMutation.error instanceof ApiClientError) {
      return updateMutation.error.error.errors?.find((e) => e.field === field)?.message;
    }
    return undefined;
  };

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <svg
            className="w-4 h-4 mr-1"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M15 19l-7-7 7-7"
            />
          </svg>
          {t('settings.backToTeam', { teamName: team.name })}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('settings.title')}</h1>
        <p className="mt-1 text-gray-600">
          {t('settings.subtitle')}
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {updateMutation.isSuccess && (
          <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
            <p className="text-green-700">{t('settings.success')}</p>
          </div>
        )}

        {updateMutation.error && !(updateMutation.error instanceof ApiClientError && updateMutation.error.error.errors) && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-red-700">
              {updateMutation.error instanceof ApiClientError
                ? updateMutation.error.error.message
                : t('settings.updateError')}
            </p>
          </div>
        )}

        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">
            {t('settings.form.name.label')}
          </label>
          <input
            type="text"
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
            minLength={2}
            maxLength={255}
            className={`mt-1 block w-full px-4 py-2 border rounded-lg focus:ring-indigo-500 focus:border-indigo-500 ${
              getFieldError('name') ? 'border-red-300' : 'border-gray-300'
            }`}
          />
          {getFieldError('name') && (
            <p className="mt-1 text-sm text-red-600">{getFieldError('name')}</p>
          )}
        </div>

        <div>
          <label htmlFor="description" className="block text-sm font-medium text-gray-700">
            {t('settings.form.description.label')}
          </label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={4}
            maxLength={2000}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
          />
          <p className="mt-1 text-sm text-gray-500">
            {t('settings.form.description.charCount', { count: description.length, max: 2000 })}
          </p>
        </div>

        <div>
          <label htmlFor="logoUrl" className="block text-sm font-medium text-gray-700">
            {t('settings.form.logoUrl.label')}
          </label>
          <input
            type="url"
            id="logoUrl"
            value={logoUrl}
            onChange={(e) => setLogoUrl(e.target.value)}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('settings.form.logoUrl.placeholder')}
          />
        </div>

        <div>
          <label htmlFor="coverImageUrl" className="block text-sm font-medium text-gray-700">
            {t('settings.form.coverImageUrl.label')}
          </label>
          <input
            type="url"
            id="coverImageUrl"
            value={coverImageUrl}
            onChange={(e) => setCoverImageUrl(e.target.value)}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('settings.form.coverImageUrl.placeholder')}
          />
        </div>

        <div className="flex items-start">
          <div className="flex items-center h-5">
            <input
              type="checkbox"
              id="isPublic"
              checked={isPublic}
              onChange={(e) => setIsPublic(e.target.checked)}
              className="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
            />
          </div>
          <div className="ml-3">
            <label htmlFor="isPublic" className="text-sm font-medium text-gray-700">
              {t('settings.form.isPublic.label')}
            </label>
            <p className="text-sm text-gray-500">
              {isPublic
                ? t('settings.form.isPublic.public')
                : t('settings.form.isPublic.private')}
            </p>
          </div>
        </div>

        <div>
          <label htmlFor="maxMembers" className="block text-sm font-medium text-gray-700">
            {t('settings.form.maxMembers.label')}
          </label>
          <input
            type="number"
            id="maxMembers"
            value={maxMembers}
            onChange={(e) => setMaxMembers(e.target.value ? Number(e.target.value) : '')}
            min={team.memberCount}
            className="mt-1 block w-full sm:w-32 px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder={t('settings.form.maxMembers.placeholder')}
          />
          <p className="mt-1 text-sm text-gray-500">
            {t('settings.form.maxMembers.hint', { count: team.memberCount })}
          </p>
        </div>

        <div className="pt-4 flex items-center justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            {tCommon('buttons.cancel')}
          </Link>
          <button
            type="submit"
            disabled={updateMutation.isPending}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            {updateMutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                {t('settings.saving')}
              </>
            ) : (
              t('settings.saveChanges')
            )}
          </button>
        </div>
      </form>

      {/* Danger Zone */}
      <div className="mt-12 pt-8 border-t border-gray-200">
        <h2 className="text-lg font-semibold text-red-600">{t('settings.dangerZone.title')}</h2>
        <p className="mt-1 text-sm text-gray-600">
          {t('settings.dangerZone.description')}
        </p>

        {!showDeleteConfirm ? (
          <button
            onClick={() => setShowDeleteConfirm(true)}
            className="mt-4 inline-flex items-center px-4 py-2 border border-red-300 rounded-md shadow-sm text-sm font-medium text-red-700 bg-white hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          >
            {t('settings.dangerZone.deleteTeam')}
          </button>
        ) : (
          <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-sm text-red-700 mb-4" dangerouslySetInnerHTML={{ __html: t('settings.dangerZone.deleteWarning', { teamName: team.name }) }} />
            <div className="flex items-center gap-3">
              <button
                onClick={handleDelete}
                disabled={deleteMutation.isPending}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50"
              >
                {deleteMutation.isPending ? (
                  <>
                    <LoadingSpinner size="sm" color="white" className="mr-2" />
                    {t('settings.dangerZone.deleting')}
                  </>
                ) : (
                  t('settings.dangerZone.confirmDelete')
                )}
              </button>
              <button
                onClick={() => setShowDeleteConfirm(false)}
                disabled={deleteMutation.isPending}
                className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                {tCommon('buttons.cancel')}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
