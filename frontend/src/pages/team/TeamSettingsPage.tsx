import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useTeam, useUpdateTeam, useDeleteTeam } from '../../hooks/useTeam';
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner';
import { ApiClientError } from '../../api/client';

export function TeamSettingsPage() {
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
    return <LoadingPage message="Loading team settings..." />;
  }

  if (error || !team) {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Error</h1>
          <p className="text-gray-600 mb-6">
            Failed to load team settings. Please try again.
          </p>
          <Link
            to="/teams"
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            Back to Teams
          </Link>
        </div>
      </div>
    );
  }

  if (team.userRole !== 'ADMIN') {
    return (
      <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h1>
          <p className="text-gray-600 mb-6">
            You need to be an admin to access team settings.
          </p>
          <Link
            to={`/teams/${teamSlug}`}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            Back to Team
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
          Back to {team.name}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">Team Settings</h1>
        <p className="mt-1 text-gray-600">
          Manage your team's profile and settings
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {updateMutation.isSuccess && (
          <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
            <p className="text-green-700">Team settings updated successfully!</p>
          </div>
        )}

        {updateMutation.error && !(updateMutation.error instanceof ApiClientError && updateMutation.error.error.errors) && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-red-700">
              {updateMutation.error instanceof ApiClientError
                ? updateMutation.error.error.message
                : 'Failed to update team. Please try again.'}
            </p>
          </div>
        )}

        <div>
          <label htmlFor="name" className="block text-sm font-medium text-gray-700">
            Team Name
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
            Description
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
            {description.length}/2000 characters
          </p>
        </div>

        <div>
          <label htmlFor="logoUrl" className="block text-sm font-medium text-gray-700">
            Logo URL
          </label>
          <input
            type="url"
            id="logoUrl"
            value={logoUrl}
            onChange={(e) => setLogoUrl(e.target.value)}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="https://example.com/logo.png"
          />
        </div>

        <div>
          <label htmlFor="coverImageUrl" className="block text-sm font-medium text-gray-700">
            Cover Image URL
          </label>
          <input
            type="url"
            id="coverImageUrl"
            value={coverImageUrl}
            onChange={(e) => setCoverImageUrl(e.target.value)}
            className="mt-1 block w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="https://example.com/cover.jpg"
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
              Public Team
            </label>
            <p className="text-sm text-gray-500">
              {isPublic
                ? 'Anyone can find and join this team'
                : 'Only invited members can join this team'}
            </p>
          </div>
        </div>

        <div>
          <label htmlFor="maxMembers" className="block text-sm font-medium text-gray-700">
            Maximum Members
          </label>
          <input
            type="number"
            id="maxMembers"
            value={maxMembers}
            onChange={(e) => setMaxMembers(e.target.value ? Number(e.target.value) : '')}
            min={team.memberCount}
            className="mt-1 block w-full sm:w-32 px-4 py-2 border border-gray-300 rounded-lg focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="No limit"
          />
          <p className="mt-1 text-sm text-gray-500">
            Current members: {team.memberCount}. Leave empty for unlimited.
          </p>
        </div>

        <div className="pt-4 flex items-center justify-end gap-3">
          <Link
            to={`/teams/${teamSlug}`}
            className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Cancel
          </Link>
          <button
            type="submit"
            disabled={updateMutation.isPending}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
          >
            {updateMutation.isPending ? (
              <>
                <LoadingSpinner size="sm" color="white" className="mr-2" />
                Saving...
              </>
            ) : (
              'Save Changes'
            )}
          </button>
        </div>
      </form>

      {/* Danger Zone */}
      <div className="mt-12 pt-8 border-t border-gray-200">
        <h2 className="text-lg font-semibold text-red-600">Danger Zone</h2>
        <p className="mt-1 text-sm text-gray-600">
          Once you delete a team, there is no going back. Please be certain.
        </p>

        {!showDeleteConfirm ? (
          <button
            onClick={() => setShowDeleteConfirm(true)}
            className="mt-4 inline-flex items-center px-4 py-2 border border-red-300 rounded-md shadow-sm text-sm font-medium text-red-700 bg-white hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
          >
            Delete Team
          </button>
        ) : (
          <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg">
            <p className="text-sm text-red-700 mb-4">
              Are you sure you want to delete <strong>{team.name}</strong>? This action cannot be
              undone and will remove all team data including rides, trips, and routes.
            </p>
            <div className="flex items-center gap-3">
              <button
                onClick={handleDelete}
                disabled={deleteMutation.isPending}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50"
              >
                {deleteMutation.isPending ? (
                  <>
                    <LoadingSpinner size="sm" color="white" className="mr-2" />
                    Deleting...
                  </>
                ) : (
                  'Yes, Delete Team'
                )}
              </button>
              <button
                onClick={() => setShowDeleteConfirm(false)}
                disabled={deleteMutation.isPending}
                className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
              >
                Cancel
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
