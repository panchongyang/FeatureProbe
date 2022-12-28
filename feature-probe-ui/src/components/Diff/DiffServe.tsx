import { ArrayChange } from 'diff';
import { Table } from 'semantic-ui-react';
import { ArrayObj, DiffResult } from './diff';
import { DiffFieldValue } from './DiffFieldValue';
import { renderField } from './renderDiff';
import conditionStyles from './RulesDiffContent.module.scss';
import { ReactNode } from 'react';
import styles from './DiffServe.module.scss';
import fieldStyles from './fields.module.scss';

interface DiffServeContentProps {
  map?: Map<string, string>;
  content:
    | {
        split?: number[];
        select?: string;
      }
    | ArrayChange<ArrayObj>[]
    | ArrayChange<number>[];
  type: 'after' | 'before';
  diffType: 'remove' | 'same' | 'modify' | 'add';
}

export const DiffServeContent: React.FC<DiffServeContentProps> = (props) => {
  const { content, type, diffType } = props;
  return (
    <Table.Row className={`${fieldStyles[`diff-item-${diffType}`]} ${conditionStyles['condition-diff-item']}`}>
      <Table.Cell>Serve</Table.Cell>
      <Table.Cell colSpan="5">
        {(() => {
          if (content instanceof Array) {
            return renderField(content, type, (value) => {
              if (value.get('select')) {
                const select = value.get('select');
                if (select?.type === 'add' && type === 'after') {
                  return <DiffFieldValue type="add" value={select.value as string} />;
                } else if (select?.type === 'remove' && type === 'before') {
                  return <DiffFieldValue type="remove" value={select.value as string} />;
                } else if (select?.type === 'same') {
                  return <DiffFieldValue type="same" value={select.value as string} />;
                }
              }
              if (value.get('split')) {
                const split = value.get('split');
                if(split && split.value instanceof Array && typeof split.value[0] === 'string') {
                  return <DiffFieldValue type={split.type} value={split.value} />;
                } else {
                  return renderField(split?.value as ArrayChange<ArrayObj>[], type, (map) => {
                    const res: ReactNode[] = [];
                    map.forEach((value) => {
                      if (value?.type === 'add' && type === 'after') {
                        res.push(<DiffFieldValue type="add" value={value.value as string} />);
                      } else if (value?.type === 'remove' && type === 'before') {
                        res.push(<DiffFieldValue type="remove" value={value.value as string} />);
                      } else if (value?.type === 'same') {
                        res.push(
                          <DiffFieldValue
                            style={{
                              background: 'rgba(33,37,41,0.08)',
                            }}
                            type="same"
                            value={value.value as string}
                          />
                        );
                      }
                    });
                    return res;
                  });
                }
              }
            });
          } else {
            if (content.select !== undefined) {
              return <DiffFieldValue value={content.select} />;
            }
            if (content.split !== undefined) {
              return <DiffFieldValue value={content.split.map((item) => item / 100 + '')} />;
            }
          }
        })()}
      </Table.Cell>
    </Table.Row>
  );
};

interface DiffServeProps {
  content: DiffResult;
}

export const DiffServe: React.FC<DiffServeProps> = (props) => {
  const { content } = props;

  return (
    <div className={styles['serve-diff']}>
      <DiffServeContent diffType="modify" content={content as ArrayChange<ArrayObj>[]} type={'before'} />
      <DiffServeContent diffType="modify" content={content as ArrayChange<ArrayObj>[]} type={'after'} />
    </div>
  );
};
